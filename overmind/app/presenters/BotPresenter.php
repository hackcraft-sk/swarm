<?php
use Nette\Application\UI;

class BotPresenter extends BaseTournamentPresenter {

	protected function createComponentUploadForm()
	{
		$form = new BaseForm;
		
		$form->addUpload("file", "choose_bot_file")
				->setRequired("you_have_to_choose_bot_file");

		$form->addSubmit('send', 'send');

		// call method signInFormSucceeded() on success
		$form->onSuccess[] = $this->uploadFormSucceeded;
		return $form;
	}
	
	public function uploadFormSucceeded(UI\Form $form) {
		if(!$this->requireLogin())
			return;

		if (!$this->getSelectedTournament()->isRunning()) {
			$this->flashMessage("You can't upload bots in tournament, which is not running.");
			return false;
		}

		if ($this->getSelectedTournament()->isCompetitionPart()) {
			$this->flashMessage("It is forbidden to upload bots in competition phase.");
			return;
		}
		
		$this->getSelectedTournament()->handleUpload($this->getUser()->getIdentity(), $form->getValues()->file);
		
		$this->flashMessage("Bot was uploaded");
	}

	public function createComponentBotDetailsForm() {
		$form = new BaseForm;

		$form->addHidden("botId");
		$form->addText("name", "Bot name");
		$form->addTextArea("comment", "Comment");
		$form->addSubmit("submit", "Submit details");

		$form->onSuccess[] = $this->onBotDetailsFormSuccess;

		return $form;
	}
	
	public function onBotDetailsFormSuccess(BaseForm $form) {
		$values = $form->getValues(true);

		try {
			$this->context->model->updateBotDetails($values['botId'], $values['name'], $values['comment']);
			$this->flashMessage("Bot details has been updated");
		} catch(\Exception $e) {
			$form->addError($e->getMessage());
		}
	}

	public function renderMy() {
		if(!$this->requireLogin())
			return;
		
		$this->template->bots = $this->getSelectedTournament()->getMyBots($this->getUser()->getIdentity());
		$this->template->matches = $this->getSelectedTournament()->getMatches($this->getUser()->getIdentity()->getId());
	}
	
	public function renderDetails($botId) {
		if(!$this->requireLogin())
			return;

		$bot = $this->context->model->getBotDetails($botId);
		$this['botDetailsForm']->setDefaults(array(
			"botId" => $botId,
			"name" => $bot['name'],
			"comment" => $bot['comment']
		));
		$this->template->bot = $bot;
	}

	public function actionSetActive($botId, $value = true) {
		if(!$this->requireLogin())
			return;

		if (!$this->getSelectedTournament()->isRunning()) {
			$this->flashMessage("You can't activate/deactivate bots in tournament, which is not running.");
			return false;
		}

		if ($this->getSelectedTournament()->isCompetitionPart()) {
			$this->flashMessage("It is forbidden to activate/deactivate bots in competition phase.");
			return;
		}

		$this->context->model->setBotActive($this->getUser()->getIdentity(), $botId, $value);
		
		$this->flashMessage(
			$value ? "Bot was activated" : "Bot was deactivated"
		);
		
		$this->redirect("my");
	}
}
?>
