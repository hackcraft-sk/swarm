<?php
/**
 * Description of TournamentSystem
 *
 * @author nixone
 */
abstract class TournamentSystem {
	/**
	 *
	 * @var Tournament
	 */
	protected $tournament;
	
	public function __construct(Tournament $tournament) {
		$this->tournament = $tournament;
	}
	
	/**
	 * Vytvori novy match, ak je to mozne
	 * 
	 * @return boolean ci sa operacia podarila (a ci pacient umrel)
	 */
	public abstract function createNextMatch($currentWaitingMatches);
	
	/**
	 * @return array (0 => POINTS_FOR_WINNER, 1 => POINTS_FOR_LOOSER)
	 */
	public abstract function getWinPoints();
	
	/**
	 * @return array (0 => POINTS_FOR_PARTIAL_WINNER, 1 => POINTS_FOR_PARTIAL_LOOSER)
	 */
	public abstract function getPartialWinPoints();
	
	/**
	 * @return int points for both of the players
	 */
	public abstract function getDrawPoints();

	public function getLadderStartTime() {
		if (time() >= $this->tournament->getCompetitionStartTime()) {
			return 0;
		}
		return time() - 24 * 3600;
	}
}
