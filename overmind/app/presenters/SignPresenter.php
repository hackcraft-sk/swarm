<?php


class SignPresenter extends BaseTournamentPresenter
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

        $form->addText('email', 'Email')
            ->addRule(BaseForm::EMAIL, "Please provide valid email")
            ->setRequired('You have to provide email for verification');

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

	public function signOnFormSucceeded($form) {
        $recaptcha = $this->context->captcha->getRecaptcha();
        $response = $recaptcha->verify($_POST['g-recaptcha-response'], $_SERVER['REMOTE_ADDR']);
        if (!$response->isSuccess()) {
            $form->addError('We couldnt verify, if you are a robot. Please tell us by checking the checkbox.');
        } else {
            $values = $form->getValues();

            if ($values->remember) {
                $this->getUser()->setExpiration('+ 14 days', FALSE);
            } else {
                $this->getUser()->setExpiration('+ 20 minutes', TRUE);
            }

            try {
                $this->context->authenticator->createAccount($values->username, $values->email, $values->password);

                $token = $this->context->emailVerification->createToken($values->email);
                $link = URL_BASE.$this->link("receiveVerificationEmail", array("token" => $token));
                $this->context->emailVerification->sendLink($values->email, $link);

                $this->flashMessage("Verification email was sent, please check your inbox");

                $this->getUser()->login($values->username, $values->password);
            } catch (Exception $e) {
                $form->addError($e->getMessage());
                return;
            }

            $this->flashMessage("Welcome {$values->username}!");
            $this->redirect('Tournament:');
        }
	}

	public function actionOut()
	{
		$this->getUser()->logout(true);
		$this->flashMessage('You have been signed out');
		$this->redirect('in');
	}

	public function renderSendVerificationEmail() {
	    $email = $this->getUser()->getIdentity()->email;

	    $token = $this->context->emailVerification->createToken($email);
        $link = URL_BASE.$this->link("receiveVerificationEmail", array("token" => $token));
        $this->context->emailVerification->sendLink($email, $link);

        $this->flashMessage("Verification email was sent, please check your inbox");
        $this->redirect("profile");
    }

    public function renderReceiveVerificationEmail($token) {
        $email = $this->context->emailVerification->decodeEmail($token);

        $this->context->authenticator->verifyEmail($email);
        if ($this->getUser()->getIdentity()->email == $email) {
            $this->getUser()->getIdentity()->isVerified = true;
        }
        $this->flashMessage("Account is now verified. Enjoy!");

        $this->redirect("profile");
    }

    public function createComponentProfileForm() {
        $form = new BaseForm;
        $form->addText('username', 'Username')
            ->setRequired('You have to enter username');

        $form->addText('email', 'Email')
            ->setRequired('You have to enter email')
            ->addRule(BaseForm::EMAIL, "You have to enter valid email");

        $form->addPassword('password', 'Password (if changing)');

        $form->addPassword("password_repeat", "Password again")
            ->addRule($form::EQUAL, "Passwords are not equal", $form['password']);

        $form->addSubmit('save', 'Save');

        $form->onSuccess[] = $this->onProfileFormSuccess;
        return $form;
    }

    public function onProfileFormSuccess($form) {
        $values = $form->getValues();

        $this->context->authenticator->updateAccount(
            $this->getUser()->getIdentity()->id,
            $values->username,
            $values->email
        );

        $this->getUser()->getIdentity()->username = $values->username;
        $this->getUser()->getIdentity()->email = $values->email;

        $this->flashMessage("Your profile was saved");

        if ($values->password) {
            $this->context->authenticator->updatePassword(
                $this->getUser()->getIdentity()->id,
                $values->password
            );
            $this->flashMessage("Your password was changed");
        }
    }

    public function renderProfile() {
        if (!$this->requireLogin()) return;

        $this['profileForm']->setValues(array(
            'username' => $this->getUser()->getIdentity()->username,
            'email' => $this->getUser()->getIdentity()->email
        ));
    }
}
