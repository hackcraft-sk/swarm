<?php
/**
 * Sign in/out presenters.
 */
class SignPresenter extends BasePresenter
{
	/**
	 * Sign-in form factory.
	 * @return Nette\Application\UI\Form
	 */
	protected function createComponentSignInForm()
	{
		$form = new BaseForm;
		$form->addText('username', 'username')
			->setRequired('you_have_to_enter_username');

		$form->addPassword('password', 'password')
			->setRequired('you_have_to_enter_password');

		$form->addCheckbox('remember', 'stay_signed_in');

		$form->addSubmit('send', 'sign_in');

		// call method signInFormSucceeded() on success
		$form->onSuccess[] = $this->signInFormSucceeded;
		return $form;
	}

	public function signInFormSucceeded($form)
	{
		$values = $form->getValues();

		if ($values->remember) {
			$this->getUser()->setExpiration('+ 14 days', FALSE);
		} else {
			$this->getUser()->setExpiration('+ 20 minutes', TRUE);
		}

		try {
			$this->getUser()->login($values->username, $values->password);
		} catch (Nette\Security\AuthenticationException $e) {
			$form->addError($e->getMessage());
			return;
		}

		$this->flashMessage("Welcome, {$values->username}!");
		$this->redirect('Tournament:');
	}
	
	protected function createComponentSignOnForm()
	{
		$form = new BaseForm;
		$form->addText('username', 'username')
			->setRequired('you_have_to_enter_username');

		$form->addPassword('password', 'password')
			->setRequired('you_have_to_enter_password');
		
		$form->addPassword("password_repeat", "password_repeat")
				->setRequired("you_have_to_enter_password")
				->addRule($form::EQUAL, "passwords_are_not_equal", $form['password']);

		$form->addCheckbox('remember', 'stay_signed_in');

		$form->addSubmit('send', 'sign_on');

		// call method signInFormSucceeded() on success
		$form->onSuccess[] = $this->signOnFormSucceeded;
		return $form;
	}

	public function signOnFormSucceeded($form)
	{
		$values = $form->getValues();

		if ($values->remember) {
			$this->getUser()->setExpiration('+ 14 days', FALSE);
		} else {
			$this->getUser()->setExpiration('+ 20 minutes', TRUE);
		}

		try {
			$this->context->authenticator->createAccount($values->username, $values->password);
			
			$this->getUser()->login($values->username, $values->password);
		} catch (Exception $e) {
			$form->addError($e->getMessage());
			return;
		}

		$this->flashMessage("Vitaj {$values->username}!");
		$this->redirect('Tournament:');
	}

	public function actionOut()
	{
		$this->getUser()->logout();
		$this->flashMessage('Signed out.');
		$this->redirect('in');
	}

}
