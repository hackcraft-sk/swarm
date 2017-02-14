<?php

abstract class BasePresenter extends Nette\Application\UI\Presenter {

	public function getSessionSection() {
		return $this->getSession()->getSection("MyLifeForAI");
	}

	public function startup() {
		parent::startup();
		
		$section = $this->getSessionSection();
		
		if(isset($section->language)) {
			$this->context->translator->setLanguage($section->language);
		}

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
        $this->template->identity = $this->getUser()->getIdentity();

		$this->template->parsedown = new Parsedown();
	}
	
	public function setLanguage($language) {
		$this->context->translator->setLanguage($language);
		$this->getSessionSection()->language = $language;
	}
	
	public function beforeRender() {
		parent::beforeRender();
		
		$this->template->loggedIn = $this->getUser()->isLoggedIn();
		$this->template->user = $this->getUser()->getIdentity();
	}
	
	public function requireLogin() {
		if(!$this->getUser()->isLoggedIn()) {
			$this->redirect("Sign:in");
			return false;
		}
		return true;
	}
}
