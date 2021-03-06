<?php

abstract class BaseTournamentPresenter extends BasePresenter {

    private $selectedTournament = false;

    public function startup() {
        parent::startup();

        if (isset($this->params["tournament"])) {
            $tournament = $this->context->model->getTournamentByCode($this->getParameter("tournament"));
            $this->selectedTournament = $tournament->getId();
        } else {
            $tournaments = $this->context->model->getTournaments();
            $tournamentIds = array_keys($tournaments);
            $tournament = $tournaments[$tournamentIds[0]];
            $this->selectedTournament = $tournament->getId();
            $this->selectTournamentByCode($tournament->getCode());
        }
    }

    public function selectTournamentByCode($code) {
        $this->redirectUrl("http://".$code.".mylifeforai.com");
    }

    public function getSelectedTournamentId() {
        return $this->selectedTournament;
    }

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

        $this->template->tournament = $this->getSelectedTournament();
        $this->template->tournaments = $this->context->model->getTournaments();

        $this->template->liveTime = $this->template->tournament->getTestStartTime();
        $this->template->isLive = time() >= $this->template->liveTime;

        $this->template->activeTournaments = $this->context->model->getActiveTournaments();
        $this->template->archivedTournaments = $this->context->model->getArchivedTournaments();
    }
}