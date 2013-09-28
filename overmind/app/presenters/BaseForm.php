<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of BaseForm
 *
 * @author nixone
 */
class BaseForm extends Nette\Application\UI\Form {
	protected function attached($presenter) {
		parent::attached($presenter);
		$this->setTranslator($presenter->context->translator);
	}
}
?>
