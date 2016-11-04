<?php
use Nette\Application\UI;

class AdminPresenter extends BaseTournamentPresenter {
	
	protected function createComponentScheduleForm()
	{
		$bots = $this->getSelectedTournament()->getActiveBotsAsKeyVal();
		
		$form = new BaseForm;
		
		$form->addSelect("host", "1st player", $bots)
				->setRequired("You have to choose the host player");
		$form->addSelect("guest", "2nd player", $bots)
				->setRequired("You have to choose the guest plyaer");
				
		$form->addSubmit('send', 'Assign to schedule');

		// call method signInFormSucceeded() on success
		$form->onSuccess[] = $this->scheduleFormSucceeded;
		return $form;
	}
	
	public function scheduleFormSucceeded(UI\Form $form) {
		if(!$this->requireAdmin())
			return;
		
		$values = $form->getValues();
		
		if($values->guest == $values->host) {
			$form->addError("They have to be different players");
			return;
		}
		
		$this->getSelectedTournament()->scheduleMatch($values->host, $values->guest);
		
		$this->flashMessage("Match was assigned to schedule");
	}
	
	protected function createComponentScheduleCombinationsForm() {
		$form = new BaseForm;
		
		$form->addText("times", "Number of matches per pair of players")
				->setRequired("You have to choose how many matches per pair")
				->setDefaultValue("1");

		$form->addCheckbox("mirror", "Do you want to mirror previous generation logic?");
		
		$form->addSubmit("send", "Generate");
		
		$form->onSuccess[] = $this->scheduleCombinationsFormSuccess;
		
		return $form;
	}
	
	public function scheduleCombinationsFormSuccess(UI\Form $form) {
		if(!$this->requireAdmin())
			return;
		
		$this->getSelectedTournament()->scheduleAllCombinations($form->getValues()->times, !!$form->getValues()->mirror);
		
		$this->flashMessage("Matches were planned");
	}
	
	protected function createComponentConfigForm() {
		$form = new BaseForm;
		
		foreach($this->context->model->getConfig() as $key => $value) {
			$form->addText($key, $key)
					->setRequired("Config value for {$key} is required")
					->setDefaultValue($value);
		}
		
		$form->addSubmit("send", "Save configuration");

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
		
		$this->flashMessage("Configuration saved");
	}
	
	protected function createComponentNewTournamentForm() {
		$form = new BaseForm;

		$form->addText("code", "Tournament code")->setRequired("Tournament code is required");
		$form->addText("name", "Tournament name")->setRequired("Tournament name is required");
		$form->addText("testStartTime", "Start time of testing part (YYYY-MM-DD H:I:S)")->setRequired("Start time of testing part is required");
		$form->addText("competitionStartTime", "Start time of competition part (YYYY-MM-DD H:I:S)")->setRequired("Start time of competition part is required");
		$form->addSubmit("submit", "Create new tournament");
	
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
				"code" => $values['code'],
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

		$form->addText("code", "Tournament code")
				->setRequired("Tournament code is required");
		$form->addText("name", "Tournament name")
				->setRequired("Tournament name is required");
		$form->addCheckbox("archived", "Archived");
		$form->addText("testStartTime", "Start time of testing part (YYYY-MM-DD H:I:S)")
				->setRequired("Start time of testing part is required");
		$form->addText("competitionStartTime", "Start time of competition part (YYYY-MM-DD H:I:S)")
				->setRequired("Start time of competition part is required");
		$form->addText("mapUrl", "URL of StarCraft map (including http://)");
		$form->addTextArea("hostStreamCode", "Embed code of host stream");
		$form->addTextArea("guestStreamCode", "Embed code of guest stream");
		$form->addTextArea("extrasJson", "Extra JSON data");
		
		foreach($this->context->translator->getLanguages() as $lang => $langName) {
			$form->addTextArea("rules_{$lang}", "Tournament rules ({$langName}, markdown)");
			$form->addTextArea("info_{$lang}", "Information about tournament ({$langName}, markdown)");
		}
		
		$form->addSubmit("submit", "Save tournament details");
	
		$form->onSuccess[] = $this->onTournamentDetailsFormSuccess;
		
		return $form;
	}
	
	public function onTournamentDetailsFormSuccess(UI\Form $form) {
		try {
			$values = $form->getValues(true);
			$values['testStartTime'] = strtotime($values['testStartTime']);
			$values['competitionStartTime'] = strtotime($values['competitionStartTime']);
			
			$info = array();
			$rules = array();
			foreach($this->context->translator->getLanguages() as $lang => $langName) {
				$info[$lang] = $values["info_{$lang}"];
				unset($values["info_{$lang}"]);
				$rules[$lang] = $values["rules_{$lang}"];
				unset($values["rules_{$lang}"]);
			}
			$values['info'] = $info;
			$values['rules'] = $rules;
			
			$this->getSelectedTournament()->update($values);
			$this->flashMessage("Tournament details were saved");
		} catch(Exception $e) {
			$form->addError($e->getMessage());
		}
	}
	
	protected function createComponentNewLinkForm() {
		$form = new BaseForm;
		
		$form->addCheckbox("tournamentWide", "Link to current tournament");
		$form->addText("url", "URL (including http://)")->setRequired("URL is required");
		
		foreach($this->context->translator->getLanguages() as $lang => $langName) {
			$form->addText("name_{$lang}", "Title ({$langName})")->setRequired("Title is required");
			$form->addText("description_{$lang}", "Description ({$langName})");
		}

		$form->addSubmit("submit", "Add");
		
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
		$form->addText("newName", "Archive as");
		$form->addCheckbox("shouldDeleteBots", "Dump bots (they stay in archive)");
		$form->addSubmit("submit", "Archive");

		$form->onSuccess[] = $this->onArchiveTournamentFormSuccess;

		return $form;
	}

	public function onArchiveTournamentFormSuccess(BaseForm $form) {
		$values = $form->getValues();

		//try {
			$this->context->model->archiveTournamentAs($values['tournamentId'], $values['newName'], $values['shouldDeleteBots']);
			$this->flashMessage("Tournament was archived");
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
		$this->flashMessage("All matches were removed");
	
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
	
	public function renderTournament($tournamentId) {
		if(!$this->requireAdmin())
			return false;
		
		$tournament = $this->context->model->getTournament($tournamentId);
		
		$values = array(
			"code" => $tournament->getCode(),
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
		foreach($tournament->getRules() as $lang => $rules) {
			$values['rules_'.$lang] = $rules;
		}
		$this['tournamentDetailsForm']->setValues($values);

		$this['archiveTournamentForm']->setValues(
			array(
				"tournamentId" => $tournament->getId(), 
				"newName" => $tournament->getName()." Archive"
			)
		);

		$this->template->t = $tournament;
	}
	
	public function actionDeleteTournament($tournamentId) {
		if(!$this->requireAdmin())
			return false;
		
		$this->context->model->deleteTournament($tournamentId);
		
		$this->flashMessage("Tournament was deleted");
		
		$this->redirectUrl($this->getHttpRequest()->getReferer());
	}

	public function renderMoveUp($tournamentId) {
		if(!$this->requireAdmin())
			return false;
		$this->context->model->moveTournament($tournamentId, -1);
		$this->redirect("tournaments");
	}

	public function renderMoveDown($tournamentId) {
		if(!$this->requireAdmin())
			return false;
		$this->context->model->moveTournament($tournamentId, 1);
		$this->redirect("tournaments");
	}
}
?>
