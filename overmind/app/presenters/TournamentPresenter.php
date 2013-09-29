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
		$this->template->ladder = array(
			"competitive" => $this->getSelectedTournament()->getLadder(true),
			"non_competitive" => $this->getSelectedTournament()->getLadder(false)
		);
		
		$this->template->competitiveSnapshotsJson = $this->getSelectedTournament()->getLadderSnapshotData("competitive");
	}
	
	public function handleRefreshLiveMatches() {
		if($this->isAjax()) {
			$this->invalidateControl("liveMatches");
		}
	}
}
?>
