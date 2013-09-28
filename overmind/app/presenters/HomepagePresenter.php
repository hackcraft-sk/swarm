<?php

/**
 * Homepage presenter.
 */
class HomepagePresenter extends BasePresenter {
	public function actionSetTournament($tournamentId) {
		$this->setSelectedTournamentId($tournamentId);
		$this->redirectUrl($this->getHttpRequest()->getReferer());
	}
	
	public function actionSetLanguage($language) {
		$this->setLanguage($language);
		$this->redirectUrl($this->getHttpRequest()->getReferer());
	}
	
	public function createComponentTestForm() {
		$form = new BaseForm;
		
		$form->addText("aaaa", 'URL', 64, 255)
				->addCondition(BaseForm::FILLED)
				->addRule(BaseForm::URL, 'nonono');
		
		$form->onSuccess[] = $this->onTestFormSuccess;
		
		return $form;
	}
	
	public function onTestFormSuccess(BaseForm $form) {
		$this->flashMessage("Form success");
	}
}
