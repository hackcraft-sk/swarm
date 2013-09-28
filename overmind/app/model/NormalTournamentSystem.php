<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of NormalTournamentSystem
 *
 * @author nixone
 */
class NormalTournamentSystem extends TournamentSystem {
	const BEFORE_COMPETITION_TIME = 1800; //0.5h
	
	public function createNextMatch() {
		$now = time();
		
		if($now >= $this->tournament->getTestStartTime() && $now < $this->tournament->getCompetitionStartTime()) {
			$diff = $this->tournament->getCompetitionStartTime() - $now;
			if($diff > self::BEFORE_COMPETITION_TIME) {
				$this->tournament->keepScheduled(5);
			} else {
				$this->tournament->keepScheduled(1);
			}
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
