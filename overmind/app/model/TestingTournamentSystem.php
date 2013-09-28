<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of TestingTournamentSystem
 *
 * @author nixone
 */
class TestingTournamentSystem extends TournamentSystem {
	const MINIMAL_COMPETITION_DURATION = 3600;
	
	public function createNextMatch() {
		$now = time();

		if($now >= $this->tournament->getTestStartTime() && $now < $this->tournament->getCompetitionStartTime()) {
			$this->tournament->keepScheduled(1);
		} else if($now >= $this->tournament->getCompetitionStartTime() && $now < $this->tournament->getCompetitionStartTime()+self::MINIMAL_COMPETITION_DURATION) {
			$this->tournament->scheduleAllCombinations();
		} else {
			return false;
		}
		
		return true;
	}

	public function getDrawPoints() {
		return -1;
	}

	public function getPartialWinPoints() {
		return array(1, -1);
	}

	public function getWinPoints() {
		return array(3, 0);
	}
}
?>
