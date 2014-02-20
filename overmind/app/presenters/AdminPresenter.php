<?php
use Nette\Application\UI;

class AdminPresenter extends BasePresenter {
	public function requireAdmin() {
		$passed = true;
		if(!parent::requireLogin())
			$passed = false;
		if(!$this->getUser()->getIdentity()->isAdmin) {
			$passed = false;
		}
		if(!$passed) {
			$this->flashMessage("Musíš byť admin");
			$this->redirect("Sign:in");
		}
		return $passed;
	}
	
	protected function createComponentScheduleForm()
	{
		$bots = $this->getSelectedTournament()->getActiveBotsAsKeyVal();
		
		$form = new BaseForm;
		
		$form->addSelect("host", "1. Hráč", $bots)
				->setRequired("Musíte zadať hosta");
		$form->addSelect("guest", "2. Hráč", $bots)
				->setRequired("Musíte zadať guesta");
				
		$form->addSubmit('send', 'Zaraď do rozvrhu');

		// call method signInFormSucceeded() on success
		$form->onSuccess[] = $this->scheduleFormSucceeded;
		return $form;
	}
	
	public function scheduleFormSucceeded(UI\Form $form) {
		if(!$this->requireAdmin())
			return;
		
		$values = $form->getValues();
		
		if($values->guest == $values->host) {
			$form->addError("Musia to byť dvaja rôzni hráči.");
			return;
		}
		
		$this->getSelectedTournament()->scheduleMatch($values->host, $values->guest);
		
		$this->flashMessage("Zápas bol zaradený do rozvrhu.");
	}
	
	protected function createComponentScheduleCombinationsForm() {
		$form = new BaseForm;
		
		$form->addText("times", "Počet zápasov na dvojicu")
				->setRequired("Hm...")
				->setDefaultValue("1");
		
		$form->addSubmit("send", "Vygeneruj");
		
		$form->onSuccess[] = $this->scheduleCombinationsFormSuccess;
		
		return $form;
	}
	
	public function scheduleCombinationsFormSuccess(UI\Form $form) {
		if(!$this->requireAdmin())
			return;
		
		$this->getSelectedTournament()->scheduleAllCombinations($form->getValues()->times);
		
		$this->flashMessage("Zápasy boli naplánované");
	}
	
	protected function createComponentConfigForm() {
		$form = new BaseForm;
		
		foreach($this->context->model->getConfig() as $key => $value) {
			$form->addText($key, $key)
					->setRequired("Config value for {$key} is required")
					->setDefaultValue($value);
		}
		
		$form->addSubmit("send", "Ulož konfiguráciu");

		$form->onSuccess[] = $this->configFormSuccess;
		
		return $form;
	}
	
	public function configFormSuccess(UI\Form $form) {
		if(!$this->requireAdmin())
			return;
		
		$values = $form->getValues();
		
		foreach($this->context->model->getConfig() as $key => $value) {
			$this->context->model->setConfig($key, $values->{$key});
		}
		
		$this->flashMessage("Konfigurácia bola uložená.");
	}
	
	protected function createComponentNewTournamentForm() {
		$form = new BaseForm;
		
		$form->addText("name", "Názov tournamentu")->setRequired("Vyžaduje sa, aby ste zadali názov tournamentu");
		$form->addText("testStartTime", "Čas začiatku testovacej etapy (YYYY-MM-DD H:I:S)")->setRequired("Vyžaduje sa, aby ste zadali čas začiatku testovacej etapy");
		$form->addText("competitionStartTime", "Čas začiatku súťažnej etapy (YYYY-MM-DD H:I:S)")->setRequired("Vyžaduje sa, aby ste zadali čas začiatku súťažnej etapy");
		$form->addSubmit("submit", "Vytvor nový tournament");
	
		$form->onSuccess[] = $this->onNewTournamentFormSuccess;
		
		return $form;
	}
	
	public function onNewTournamentFormSuccess(UI\Form $form) {
		$values = $form->getValues(true);
		
		try {
			$testStartTime = strtotime($values['testStartTime']);
			$competitionStartTime = strtotime($values['competitionStartTime']);
			
			if($testStartTime === FALSE) {
				throw new Exception("Test start time has wrong format");
			}
			
			if($competitionStartTime === FALSE) {
				throw new Exception("Competition start time has wrong format");
			}
			
			$data = array(
				"name" => $values['name'],
				"testStartTime" => $testStartTime,
				"competitionStartTime" => $competitionStartTime
			);
			$this->context->model->createNewTournament($data);
			$this->setSelectedTournamentId($data['id']);
		} catch(\Exception $e) {
			$form->addError($e->getMessage());
		}
	}
	
	protected function createComponentTournamentDetailsForm() {
		$form = new BaseForm;
		
		$form->addText("name", "Názov tournamentu")
				->setRequired("Vyžaduje sa, aby ste zadali názov tournamentu");
		$form->addCheckbox("archived", "Archívovaný");
		$form->addText("testStartTime", "Čas začiatku testovacej etapy (YYYY-MM-DD H:I:S)")
				->setRequired("Vyžaduje sa, aby ste zadali čas začiatku testovacej etapy");
		$form->addText("competitionStartTime", "Čas začiatku súťažnej etapy (YYYY-MM-DD H:I:S)")
				->setRequired("Vyžaduje sa, aby ste zadali čas začiatku súťažnej etapy");
		$form->addText("mapUrl", "URL SC Mapy (s http://)");
		$form->addTextArea("hostStreamCode", "Kód streamu hosťujúceho stroja");
		$form->addTextArea("guestStreamCode", "Kód streamu hosťovského stroja");
		$form->addTextArea("extrasJson", "Extra JSON dáta");
		
		foreach($this->context->translator->getLanguages() as $lang => $langName) {
			$form->addTextArea("info_{$lang}", "Pravidlá tournamentu ({$langName})");
		}
		
		$form->addSubmit("submit", "Uložiť detaily tournamentu");
	
		$form->onSuccess[] = $this->onTournamentDetailsFormSuccess;
		
		return $form;
	}
	
	public function onTournamentDetailsFormSuccess(UI\Form $form) {
		try {
			$values = $form->getValues(true);
			$values['testStartTime'] = strtotime($values['testStartTime']);
			$values['competitionStartTime'] = strtotime($values['competitionStartTime']);
			
			$info = array();
			foreach($this->context->translator->getLanguages() as $lang => $langName) {
				$info[$lang] = $values["info_{$lang}"];
				unset($values["info_{$lang}"]);
			}
			$values['info'] = $info;
			
			$this->getSelectedTournament()->update($values);
			$this->flashMessage("Detaily tournamentu boli uložené");
		} catch(Exception $e) {
			$form->addError($e->getMessage());
		}
	}
	
	protected function createComponentNewLinkForm() {
		$form = new BaseForm;
		
		$form->addCheckbox("tournamentWide", "Naviazať na tournament");
		$form->addText("url", "URL (s http://)")->setRequired("URL sa vyžaduje");
		
		foreach($this->context->translator->getLanguages() as $lang => $langName) {
			$form->addText("name_{$lang}", "Názov ({$langName})")->setRequired("Názov sa vyžaduje");
			$form->addText("description_{$lang}", "Popis ({$langName})");
		}

		$form->addSubmit("submit", "Pridaj");
		
		$form->onSuccess[] = $this->onNewLinkFormSuccess;
		
		return $form;
	}
	
	public function onNewLinkFormSuccess(BaseForm $form) {
		$values = $form->getValues(true);
		
		try {
			$names = array();
			$descriptions = array();
			
			foreach($this->context->translator->getLanguages() as $lang => $langName) {
				$names[$lang] = $values["name_{$lang}"];
				$descriptions[$lang] = $values["description_{$lang}"];
			}
			
			$this->context->link->createNew($values['url'], $names, $descriptions, ($values['tournamentWide'] ? $this->getSelectedTournamentId() : null));
		} catch(Exception $e) {
			$form->addError($e->getMessage());
		}
	}

	public function createComponentArchiveTournamentForm() {
		$form = new BaseForm;

		$form->addHidden("tournamentId");
		$form->addText("newName", "Archívovať ako");
		$form->addCheckbox("shouldDeleteBots", "Vyčistiť botov (v archíve ostanú)");
		$form->addSubmit("submit", "Archívovať");

		$form->onSuccess[] = $this->onArchiveTournamentFormSuccess;

		return $form;
	}

	public function onArchiveTournamentFormSuccess(BaseForm $form) {
		$values = $form->getValues();

		//try {
			$this->context->model->archiveTournamentAs($values['tournamentId'], $values['newName'], $values['shouldDeleteBots']);
			$this->flashMessage("Tournament bol úspešne archívovaný");
		//} catch(\Exception $e) {
		//	$form->addError($e->getMessage());
		//}
	}
	
	public function actionDeleteLink($id) {
		$this->context->link->delete($id);
		$this->redirect("console");
	}
	
	public function actionClearAllMatches() {
		if(!$this->requireAdmin())
			return;
		
		$this->getSelectedTournament()->clearSchedule();		
		$this->flashMessage("Všetky zápasy boli vymazané.");
	
		$this->redirect("console");
	}
	
	public function renderConsole() {
		if(!$this->requireAdmin())
			return false;
	}
	
	public function renderLinks() {
		if(!$this->requireAdmin())
			return false;
		
		$this->template->links = $this->context->link->getAll($this->getSelectedTournamentId());
	}
	
	public function renderTournament() {
		if(!$this->requireAdmin())
			return false;
		
		$tournament = $this->getSelectedTournament();
		
		$values = array(
			"name" => $tournament->getName(),
			"testStartTime" => date("Y-m-d H:i:s", $tournament->getTestStartTime()),
			"competitionStartTime" => date("Y-m-d H:i:s", $tournament->getCompetitionStartTime()),
			"hostStreamCode" => $tournament->getHostStreamCode(),
			"guestStreamCode" => $tournament->getGuestStreamCode(),
			"extrasJson" => $tournament->getExtrasJson(),
			"mapUrl" => $tournament->getMapUrl(),
			"archived" => $tournament->isArchived(),
		);
		
		foreach($tournament->getInfo() as $lang => $info) {
			$values["info_{$lang}"] = $info;
		}
		$this['tournamentDetailsForm']->setValues($values);

		$this['archiveTournamentForm']->setValues(
			array(
				"tournamentId" => $tournament->getId(), 
				"newName" => $tournament->getName()." Archive"
			)
		);
	}
	
	public function actionGoToTournament($tournamentId) {
		$this->setSelectedTournamentId($tournamentId);
		$this->redirect("tournament");
	}
	
	public function actionDeleteTournament($tournamentId) {
		if(!$this->requireAdmin())
			return false;
		
		$this->context->model->deleteTournament($tournamentId);
		
		$this->flashMessage("Tournament bol úspešne odstránený");
		
		$this->redirectUrl($this->getHttpRequest()->getReferer());
	}
}
?>
