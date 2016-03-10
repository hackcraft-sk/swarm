<?php
/**
 * Description of TournamentPresenter
 *
 * @author nixone
 */
class TournamentPresenter extends BaseTournamentPresenter {

	public function renderDefault() {
		$this->template->playing = $this->getSelectedTournament()->getPlayingMatch();
		$this->template->matches = $this->getSelectedTournament()->getMatches();
	}
	
	public function renderMatches() {
		$this->template->matches = $this->getSelectedTournament()->getMatches();
	}
	
	public function renderInfo() {
		$this->template->links = $this->context->link->getAll($this->getSelectedTournamentId());
	}
	
	public function renderLadder() {
		$tournament = $this->getSelectedTournament();
		$ladder = $tournament->getLadder($tournament->getSystem()->getLadderStartTime());

		foreach($ladder as &$user) {
			$user['achievements'] = $tournament->getAchievements()->getAchievementsForUser($user['id']);
		}

		$this->template->ladder = $ladder;

		$this->template->snapshotsJson = $tournament->getLadderSnapshotData();
	}
	
	public function handleRefreshLiveMatches() {
		if($this->isAjax()) {
			$this->invalidateControl("liveMatches");
		}
	}
}
?>
