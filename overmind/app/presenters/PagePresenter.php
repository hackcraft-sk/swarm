<?php
use Nette\Application\UI;

class PagePresenter extends BaseTournamentPresenter {

    public function renderShow($id) {
        $this->template->content = $this->context->pages->getContent($id);
    }

    public function renderAdminList() {
        if (!$this->requireAdmin()) return;
        $this->template->pages = $this->context->pages->getAll();
    }


    protected function createComponentNewPageForm() {
        $form = new BaseForm;

        $form->addText('slug', 'Slug')
            ->setRequired('You have to put a slug')
            ->addRule(BaseForm::PATTERN, 'Slug has to contain alphanumerics or -', '[a-zA-Z0-9\-]*');

        $form->addTextArea('content', 'Content')
            ->setRequired('You have to provide a content');

        $form->addSubmit('create', 'Create new page');

        // call method signInFormSucceeded() on success
        $form->onSuccess[] = $this->newPageFormSucceeded;
        return $form;
    }

    public function newPageFormSucceeded(UI\Form $form) {
        if(!$this->requireAdmin())
            return;

        $values = $form->getValues();
        $this->context->pages->create($values->slug, $values->content);

        $this->flashMessage("Page was created");
    }

    public function actionAdminDelete($slug) {
        if(!$this->requireAdmin())
            return;

        $this->context->pages->delete($slug);
        $this->flashMessage("Page was deleted");
        $this->redirect("adminList");
    }

    protected function createComponentUpdatePageForm() {
        $form = new BaseForm;

        $form->addHidden('originalSlug');

        $form->addText('slug', 'Slug')
            ->setRequired('You have to put a slug')
            ->addRule(BaseForm::PATTERN, 'Slug has to contain alphanumerics or -', '[a-zA-Z0-9\-]*');

        $form->addTextArea('content', 'Content')
            ->setRequired('You have to provide a content');

        $form->addSubmit('update', 'Update');

        // call method signInFormSucceeded() on success
        $form->onSuccess[] = $this->updatePageFormSucceeded;
        return $form;
    }

    public function updatePageFormSucceeded(UI\Form $form) {
        if(!$this->requireAdmin())
            return;

        $values = $form->getValues();
        $this->context->pages->update($values->originalSlug, $values->slug, $values->content);

        $this->flashMessage("Page was updated");
    }

    public function renderAdminUpdate($slug) {
        if(!$this->requireAdmin())
            return;

        $content = $this->context->pages->getContent($slug);
        $this->template->content = $content;

        $this['updatePageForm']->setValues(array(
            'originalSlug' => $slug,
            'slug' => $slug,
            'content' => $content
        ));
    }
}