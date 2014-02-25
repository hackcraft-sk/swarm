<?php
class Achievements {
	private $database;
	private $tournamentId;
	private $achievements = array();

	public function __construct(Nette\Database\Connection $database, $tournamentId) {
		$this->database = $database;
		$this->tournamentId = $tournamentId;
		$this->loadAchievements();
	}

	private function loadAchievements() {
		//$stmt = $this->database->prepare("SELECT `achievements`.* FROM `achievements_in_tournaments` AS ait LEFT JOIN `achievements` ON (`achievements`.`id`=ait.`achievementId`) WHERE ait.`tournamentId`=?");
		//$stmt->bindParam(1, $this->tournamentId);
		// SO FAR LOAD ALL OF THEM
		$stmt = $this->database->prepare("SELECT * FROM `achievements` WHERE 1");

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}

		$result = $stmt->fetchAll(PDO::FETCH_CLASS, "Achievement");
		foreach($result as $achievement) {
			$this->achievements[$achievement->getId()] = $achievement;
		}
	}

	public function getAchievement($id) {
		if(!isset($this->achievements[$id])) {
			throw new Exception("Achievement {$id} doesn't exist!");
		}
		return $this->achievements[$id];
	}

	public function getAchievementsForMatch($matchId, $userId) {
		$sql = <<<SQL
			SELECT DISTINCT
				achievements.id AS `id`
			FROM
				achievements_earned AS earned
				LEFT JOIN achievements ON (earned.achievementId=achievements.id)
			WHERE
				earned.botId IN(
					SELECT `bots`.`id`
					FROM `bots`
					WHERE `bots`.`userId`=? AND `bots`.`tournamentId`=?
				) AND earned.matchId=?
SQL;

		$stmt = $this->database->prepare($sql);
		$stmt->bindParam(1, $userId);
		$stmt->bindParam(2, $this->tournamentId);
		$stmt->bindParam(3, $matchId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}

		$result = array();

		while($row = $stmt->fetch()) {
			$achievement = $this->getAchievement($row['id']);
			$result[$achievement->getId()] = $achievement;
		}

		return $result;
	}

	public function getAchievementsForUser($userId) {
		$sql = <<<SQL
			SELECT DISTINCT
				achievements.id AS `id`
			FROM
				achievements_earned AS earned
				LEFT JOIN achievements ON (earned.achievementId=achievements.id)
			WHERE
				earned.botId IN(
					SELECT `bots`.`id`
					FROM `bots`
					WHERE `bots`.`userId`=? AND `bots`.`tournamentId`=?
				)
SQL;

		$stmt = $this->database->prepare($sql);
		$stmt->bindParam(1, $userId);
		$stmt->bindParam(2, $this->tournamentId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}

		$result = array();

		while($row = $stmt->fetch()) {
			$achievement = $this->getAchievement($row['id']);
			$result[$achievement->getId()] = $achievement;
		}

		return $result;
	}

	public function addEarnedAchievement($botId, $matchId, Achievement $achievement) {
		$achievementId = $achievement->getId();
		$stmt = $this->database->prepare("INSERT INTO `achievements_earned` (`botId`, `matchId`, `achievementId`) VALUES(?, ?, ?)");
		$stmt->bindParam(1, $botId);
		$stmt->bindParam(2, $matchId);
		$stmt->bindParam(3, $achievementId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}
}

class Achievement {
	private $id;
	private $name;
	private $description;
	private $isVisible;
	private $nature;
	private $abbr;

	public function getId() {
		return $this->id;
	}

	public function getAbbr() {
		return $this->abbr;
	}

	public function getName() {
		return $this->name;
	}

	public function isVisible() {
		return !!$this->isVisible;
	}

	public function getDescription() {
		return $this->description;
	}

	public function getImageLocalFile() {
		return dirname(dirname(dirname(__FILE__)))."/www/achievements/{$this->id}.png";
	}

	public function getImageURL() {
		return "http://scmai.hackcraft.sk/achievements/{$this->id}.png";
	}

	public function isGood() {
		return $this->nature > 0;
	}

	public function isBad() {
		return $this->nature < 0;
	}
}