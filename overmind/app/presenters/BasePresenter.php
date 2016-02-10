<?php
/**
 * Base presenter for all application presenters.
 */
abstract class BasePresenter extends Nette\Application\UI\Presenter {
	private $selectedTournament = false;
	
	public function startup() {
		parent::startup();
		
		$session = $this->getSession();
		$section = $session->getSection("SCMAISystem");
		
		if(!isset($section->selectedTournament)) {
			$tournaments = $this->context->model->getTournaments();
			$tournamentIds = array_keys($tournaments);
			$section->selectedTournament = $tournamentIds[0];
		}
		
		if(isset($section->language)) {
			$this->context->translator->setLanguage($section->language);
		}
		
		$this->selectedTournament = $section->selectedTournament;
		
		$this->template->setTranslator($this->context->translator);
		
		$this->template->lang = $this->context->translator->getLanguage();
		$this->template->languages = $this->context->translator->getLanguages();
		
		$this->template->registerHelper("shortDuration", function($time) {
			if($time < 60) {
				return $time."s";
			}
			$minutes = $time / 60;
			$seconds = $time % 60;
			return sprintf("%d", $minutes)."m ".sprintf("%d", $seconds)."s";
		});
		
		$this->template->registerHelper("startTime", function($time) {
			$output = "";
			$now = time();
			$daysBefore = (int)(($now-$time)/(3600*24));
			if($daysBefore > 0) {
				return "-".$daysBefore."d ";
			}
			
			return date("H:i", $time);
		});
		
		$this->template->registerHelper("printf", "sprintf");
		
		$this->template->username = $this->getUser()->isLoggedIn() ? $this->getUser()->getIdentity()->username : "anonymous";

		$this->template->parsedown = new Parsedown();
	}
	
	public function setLanguage($language) {
		$this->context->translator->setLanguage($language);
		$section = $this->getSession()->getSection("SCMAISystem");
		$section->language = $language;
	}
	
	public function setSelectedTournamentId($id) {
		$this->selectedTournament = $id;
		
		$session = $this->getSession();
		$section = $session->getSection("SCMAISystem");
		$section->selectedTournament = $this->selectedTournament;
	}
	
	public function getSelectedTournamentId() {
		return $this->selectedTournament;
	}
	
	/**
	 * 
	 * @return Tournament
	 */
	public function getSelectedTournament() {
		$tournament = $this->context->model->getTournament($this->getSelectedTournamentId());
		if($tournament == null) {
			$tournaments = $this->context->model->getTournaments();
			$tournamentIds = array_keys($tournaments);
			$this->selectedTournament = $tournamentIds[0];
			$tournament = $this->context->model->getTournament($this->getSelectedTournamentId());
		}
		return $tournament;
	}
	
	public function beforeRender() {
		parent::beforeRender();
		
		$this->template->loggedIn = $this->getUser()->isLoggedIn();
		$this->template->user = $this->getUser()->getIdentity();

		$this->template->tournament = $this->getSelectedTournament();
		$this->template->tournaments = $this->context->model->getTournaments();

		$this->template->liveTime = $this->template->tournament->getTestStartTime();
		$this->template->isLive = time() >= $this->template->liveTime;

		$this->template->activeTournaments = $this->context->model->getActiveTournaments();
	}
	
	public function requireLogin() {
		if(!$this->getUser()->isLoggedIn()) {
			$this->redirect("Sign:in");
			return false;
		}
		return true;
	}
}
