<?php
/**
 * Description of TournamentPresenter
 *
 * @author nixone
 */
class TournamentPresenter extends BasePresenter {
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
		$ladder = $this->getSelectedTournament()->getLadder();

		foreach($ladder as &$user) {
			$user['achievements'] = $this->getSelectedTournament()->getAchievements()->getAchievementsForUser($user['id']);
		}

		$this->template->ladder = $ladder;

		$this->template->snapshotsJson = $this->getSelectedTournament()->getLadderSnapshotData();
	}
	
	public function handleRefreshLiveMatches() {
		if($this->isAjax()) {
			$this->invalidateControl("liveMatches");
		}
	}
}
?>
