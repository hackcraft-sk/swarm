<?php
include_once "Achievements.php";

/**
 * Description of Tournament
 *
 * @author nixone
 */
class Tournament {
	const LADDER_SNAPSHOT_LENGTH = 86400;
	
	/**
	 *
	 * @var \Nette\Database\Connection
	 */
	private $database;
	
	/**
	 *
	 * @var array
	 */
	private $data;

	/**
	 *
	 * @var TournamentSystem
	 */
	private $system;

	private $achievements;
	
	public function __construct(\Nette\Database\Connection $database, $data) {
		$this->database = $database;
		$this->data = array();
		
		foreach($data as $k=>$v) {
			$this->data[$k] = $v;
		}
		
		$this->data['info'] = json_decode($this->data['info'], true);
		$this->data['rules'] = json_decode($this->data['rules'], true);
		$this->data['extras'] = json_decode($this->data['extrasJson'], true);
		
		$systemClass = $this->data['system'];
		$this->system = new $systemClass($this);

		$this->achievements = new Achievements($database, $this->data['id']);
	}

	public function getCode() {
		return $this->data['code'];
	}

	public function getPriority() {
		return $this->data['priority'];
	}
	
	public function getId() {
		return $this->data['id'];
	}
	
	public function getName() {
		return $this->data['name'];
	}
	
	public function getTestStartTime() {
		return $this->data['testStartTime'];
	}

	public function isCompetitionPart() {
		return time() >= $this->getCompetitionStartTime();
	}

	public function isTestingPart() {
		return !$this->isCompetitionPart() && time() >= $this->getTestStartTime();
	}

	public function isRunning() {
		return $this->isTestingPart() || $this->isCompetitionPart();
	}
	
	public function getCompetitionStartTime() {
		return $this->data['competitionStartTime'];
	}
	
	public function getHostStreamCode() {
		return $this->data['hostStreamCode'];
	}
	
	public function getGuestStreamCode() {
		return $this->data['guestStreamCode'];
	}
	
	public function getInfo() {
		return $this->data['info'];
	}

	public function getInfoJson() {
		return json_encode($this->getInfo(), true);
	}

	public function getRules() {
		return $this->data['rules'];
	}
	public function getRulesJson() {
		return json_encode($this->getRules(), true);
	}

	
	public function getExtras() {
		return $this->data['extras'];
	}
	
	public function getExtrasJson() {
		return $this->data['extrasJson'];
	}
	
	public function getMapUrl() {
		return $this->data['mapUrl'];
	}

	public function isArchived() {
		return $this->data['archived'];
	}

	public function getSystemClassName() {
		return $this->data['system'];
	}

	public function getPointsDependingOnAchievements($hostAchievements, $guestAchievements) {
		if(isset($hostAchievements['victory']) && isset($guestAchievements['defeat'])) { // WIN LOST
			list($hostPoints, $guestPoints) = $this->getSystem()->getWinPoints();
			$result = 1;
		} else if(isset($hostAchievements['defeat']) && isset($guestAchievements['victory'])) { // LOST WIN
			list($guestPoints, $hostPoints) = $this->getSystem()->getWinPoints();
			$result = -1;
		} else if(
			(isset($hostAchievements['defeat']) && isset($guestAchievements['defeat'])) ||
			(isset($hostAchievements['draw']) && isset($guestAchievements['draw']))
		) {
			$hostPoints = $guestPoints = $this->getSystem()->getDrawPoints();
			$result = 0;
		} else {
			throw new Exception("Wrong victory/defeat arrangement!");
		}

		return array($result, $hostPoints, $guestPoints);
	}
	
	public function update($data) {
		foreach($data as $k=>$v) {
			$this->data[$k] = $v;
		}
		
		if(isset($data['extras'])) {
			$this->data['extrasJson'] = json_encode($data['extras']);
		}

		$info = $this->getInfoJson();
		$rules = $this->getRulesJson();
		
		$stmt = $this->database->prepare("UPDATE `tournaments` SET `code`=?, `name`=?, `testStartTime`=?, `competitionStartTime`=?, `info`=?, `rules`=?, `hostStreamCode`=?, `guestStreamCode`=?, `extrasJson`=?, `mapUrl`=?, `archived`=?, `priority`=? WHERE `id`=?");
		$stmt->bindParam(1, $this->data['code']);
		$stmt->bindParam(2, $this->data['name']);
		$stmt->bindParam(3, $this->data['testStartTime']);
		$stmt->bindParam(4, $this->data['competitionStartTime']);
		$stmt->bindParam(5, $info);
		$stmt->bindParam(6, $rules);
		$stmt->bindParam(7, $this->data['hostStreamCode']);
		$stmt->bindParam(8, $this->data['guestStreamCode']);
		$stmt->bindParam(9, $this->data['extrasJson']);
		$stmt->bindParam(10, $this->data['mapUrl']);
		$stmt->bindParam(11, $this->data['archived']);
		$stmt->bindParam(12, $this->data['priority']);
		$stmt->bindParam(13, $this->data['id']);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}
	
	public function handleUpload(Nette\Security\Identity $identity, Nette\Http\FileUpload $upload) {
		$tournamentId = $this->data['id'];		
		$now = time();
		
		if($now >= $this->data['competitionStartTime']) {
			throw new Exception("Bot upload is freezed");
		}
		
		$id = $identity->getId();

		$name = $upload->getName();
		$parts = explode(".", $name);
		$extension = $parts[count($parts)-1];
		
		if(!in_array($extension, Model::$extensions)) {
			throw new Exception("Unrecognized extension {$extension}.");
		}
		
		$data = $identity->getData();
		
		$fileName = $data['username']."_".date("Ymd_His", $now).".".$extension;
		
		$path = BOTS_PATH."/".$fileName;
		
		$upload->move($path);
		
		$true = true;
		
		$stmt = $this->database->prepare("INSERT INTO `bots` (`userId`, `tournamentId`, `uploadTime`, `isActive`, `fileName`, `name`, `comment`) VALUES(?, ?, ?, ?, ?, ?, '')");
		$stmt->bindParam(1, $id);
		$stmt->bindParam(2, $tournamentId);
		$stmt->bindParam(3, $now);
		$stmt->bindParam(4, $true);
		$stmt->bindParam(5, $fileName);
		$stmt->bindParam(6, $data['username']);
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
	}
	
	public function getMyBots(Nette\Security\Identity $identity) {
		$id = $identity->getId();
		$tournamentId = $this->data['id'];
		
		$stmt = $this->database->prepare("SELECT * FROM `bots` WHERE `userId`=? AND `tournamentId`=? ORDER BY `uploadTime` DESC");
		$stmt->bindParam(1, $id);
		$stmt->bindParam(2, $tournamentId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
		
		$bots = array();
		
		while($row = $stmt->fetch()) {
			$bots[$row['id']] = $row;
		}
		
		return $bots;
	}
	
	public function getActiveBots() {
		$tournamentId = $this->data['id'];
		
		$stmt = $this->database->prepare("SELECT * FROM `users`");
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
		
		$bots = array();
		
		while($user = $stmt->fetch()) {
			$id = $user['id'];
			$stmt2 = $this->database->prepare("SELECT * FROM `bots` WHERE `userId`=? AND `tournamentId`=? AND `isActive`=1 ORDER BY `uploadTime` DESC LIMIT 0,1");
			$stmt2->bindParam(1, $id);
			$stmt2->bindParam(2, $tournamentId);
			
			if(!$stmt2->execute() || $stmt2->rowCount() != 1) {
				continue;
			}
			
			$bots[$id] = $stmt2->fetch();
			$bots[$id]['username'] = $user['username'];
		}
		
		return $bots;
	}
	
	public function getActiveBotsAsKeyVal() {
		$bots = $this->getActiveBots();
		
		$result = array();
		
		foreach($bots as $key => $bot) {
			$result[$key] = $bot['username'];
		}
		
		return $result;
	}
	
	public function getMatches($userId = 0) {
		$id = $this->data['id'];
		
		$query = <<<SQL
			SELECT 
				matches.*, 
				host.username AS hostName,
				guest.username AS guestName,
				(
					SELECT GROUP_CONCAT(DISTINCT `ae`.achievementId SEPARATOR ' ') 
					FROM achievements_earned AS `ae` 
					WHERE `ae`.matchId=matches.id AND `ae`.botId=matches.hostBotId
				) AS `hostBotAchievementsString`,
				(
					SELECT GROUP_CONCAT(DISTINCT `ae2`.achievementId SEPARATOR ' ') 
					FROM achievements_earned AS `ae2` 
					WHERE `ae2`.matchId=matches.id AND `ae2`.botId=matches.guestBotId
				) AS `guestBotAchievementsString`
			FROM 
				matches
				LEFT JOIN users AS host ON(matches.hostUserId = host.id) 
				LEFT JOIN users AS guest ON(matches.guestUserId = guest.id) 
			WHERE 
				`tournamentId`=?
				%USER_CONDITION%
			ORDER BY id DESC
			LIMIT 0, 250
SQL;
		if($userId == 0) {
			$userCondition = "";
		} else {
			$userCondition = " AND (`matches`.`hostUserId`=? OR `matches`.`guestUserId`=?)";
		}

		$query = str_replace("%USER_CONDITION%", $userCondition, $query);

		$stmt = $this->database->prepare($query);
		$stmt->bindParam(1, $id);
		if($userId != 0) {
			$stmt->bindParam(2, $userId);
			$stmt->bindParam(3, $userId);
		}
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
	
		$matches = array();

		while($row = $stmt->fetch()) {
			$realFile = REPLAYS_PATH."/".$row['id'].".rep";
			$urlFile = REPLAYS_URL."/".$row['id'].".rep";

			if(file_exists($realFile)) {
				$row['replayUrl'] = $urlFile;
			}

			$row['hostBotAchievements'] = $this->getAchievementsFromString($row['hostBotAchievementsString']);
			$row['guestBotAchievements'] = $this->getAchievementsFromString($row['guestBotAchievementsString']);

			$matches[] = $row;
		}

		return $matches;
	}
	
	private function getLadderQuery($minimalStartTime = 0) {
$query = <<<SQL
			SELECT
				users.*,
				(
					SELECT COUNT(*) FROM matches
					LEFT JOIN users AS host ON (host.id = matches.hostUserId)
					LEFT JOIN users AS guest ON (guest.id = matches.guestUserId)
					WHERE	(
								(
									users.id = matches.hostUserId AND 
									matches.result = 1
								) OR (
									users.id = matches.guestUserId AND 
									matches.result = -1
								)
							)
					AND matches.state = 'FINISHED'
					AND matches.tournamentId = ?
					%CONDITION%
				) AS matchesWon,
				(
					SELECT COUNT(*) FROM matches
					LEFT JOIN users AS host ON (host.id = matches.hostUserId)
					LEFT JOIN users AS guest ON (guest.id = matches.guestUserId)
					WHERE	(
								users.id = matches.hostUserId OR 
								users.id = matches.guestUserId
							)
					AND matches.state = 'FINISHED'
					AND matches.tournamentId = ?
					%CONDITION%
				) AS matchesTotal,

				(
					SELECT COUNT(*) FROM matches 
					LEFT JOIN users AS host ON (host.id = matches.hostUserId)
					LEFT JOIN users AS guest ON (guest.id = matches.guestUserId)
					WHERE	(
								users.id = matches.hostUserId OR 
								users.id = matches.guestUserId
							) 
					AND matches.state = 'FINISHED'
					AND matches.result = 0
					AND matches.tournamentId = ?
					%CONDITION%
				) AS matchesDraw,
				
				(
					SELECT SUM(matches.hostPoints) FROM matches
					LEFT JOIN users AS host ON (host.id = matches.hostUserId)
					LEFT JOIN users AS guest ON (guest.id = matches.guestUserId)
					WHERE users.id = matches.hostUserId
					AND matches.tournamentId = ?
					%CONDITION%
				) AS pointsAsHost,
				
				(
					SELECT SUM(matches.guestPoints) FROM matches
					LEFT JOIN users AS host ON (host.id = matches.hostUserId)
					LEFT JOIN users AS guest ON (guest.id = matches.guestUserId)
					WHERE users.id = matches.guestUserId
					AND matches.tournamentId = ?
					%CONDITION%
				) AS pointsAsGuest

				FROM users
				
				WHERE EXISTS(
					SELECT * FROM `bots` WHERE `isActive`=1 AND `userId`=`users`.`id` AND `tournamentId`= ?
				)
SQL;

		$condition = "AND matches.startTime >= ".(int)$minimalStartTime;

		$query = str_replace("%CONDITION%", $condition, $query);
		
		return $query;
	}
	
	public function getLadder($minimalStartTime = 0) {
		$id = $this->data['id'];
	
		$stmt = $this->database->prepare($this->getLadderQuery($minimalStartTime));
		$stmt->bindParam(1, $id);
		$stmt->bindParam(2, $id);
		$stmt->bindParam(3, $id);
		$stmt->bindParam(4, $id);
		$stmt->bindParam(5, $id);
		$stmt->bindParam(6, $id);
		
		if(!$stmt->execute())
			throw new Exception("DB error");

		$rows = array();
		while($row = $stmt->fetch()) {
			$row['points'] = $row['pointsAsHost']+$row['pointsAsGuest'];
			$row['pointsPerMatch'] = $row['matchesTotal'] > 0 ? $row['points'] / (float)$row['matchesTotal'] : 0;
			$rows[] = $row;
		}

		usort($rows, function($a, $b) {
			if($a['pointsPerMatch'] < $b['pointsPerMatch']) {
				return 1;
			} else if($a['pointsPerMatch'] > $b['pointsPerMatch']) {
				return -1;
			}

			if($a['points'] < $b['points']) {
				return 1;
			} else if($a['points'] > $b['points']) {
				return -1;
			}
			
			if($a['matchesTotal'] == 0) {
				if($b['matchesTotal'] == 0) {
					return 0;
				}
				return -1;
			} else if($b['matchesTotal'] == 0) {
				return 1;
			}

			$aRatio = $a['matchesWon'] / $a['matchesTotal'];
			$bRatio = $b['matchesWon'] / $b['matchesTotal'];

			if($aRatio > $bRatio) {
				return -1;
			} else if($aRatio < $bRatio) {
				return 1;
			}
			return 0;
		});

		return $rows;
	}
	
	public function takeLadderSnapshot() {
		$now = time();
		
		$snapshot = $this->getLadder(true, $now-self::LADDER_SNAPSHOT_LENGTH);

		$snapshotJson = json_encode($snapshot);
		$tournamentId = $this->data['id'];
		
		$stmt = $this->database->prepare("INSERT INTO `ladder_snapshots` (`tournamentId`, `time`, `ladder`) VALUES(?, ?, ?)");
		$stmt->bindParam(1, $tournamentId);
		$stmt->bindParam(2, $now);
		$stmt->bindParam(3, $snapshotJson);
		
		if(!$stmt->execute()) {
			throw new \Exception("DB: Query error");
		}
	}
	
	public function scheduleMatch($hostId, $guestId) {
		$id = $this->data['id'];
		
		$stmt = $this->database->prepare("INSERT INTO matches (tournamentId, hostUserId, guestUserId) VALUES(?, ?, ?)");
		$stmt->bindParam(1, $id);
		$stmt->bindParam(2, $hostId);
		$stmt->bindParam(3, $guestId);
		
		if(!$stmt->execute())
			throw new Exception("DB error");

		return array(
			"id" => $this->database->lastInsertId(),
			"tournament" => $id,
			"host" => $hostId,
			"guest" => $guestId
		);
	}

	public function scheduleAllCombinations($times = 1, $mirror = false) {
		$combinations = array();
		$bots = array_keys($this->getActiveBots());

        if ($mirror) {
            $bots = array_reverse($bots);
        }

		for($i=0; $i < $times; $i++) {
			for($j=0; $j < count($bots); $j++) {
				for($k=$j+1; $k < count($bots); $k++) {
					$combinations[] = array($bots[$j], $bots[$k]);
				}
			}

			$bots = array_reverse($bots);
		}

		shuffle($combinations);

		foreach($combinations as $combination) {
			$this->scheduleMatch($combination[0], $combination[1]);
		}
	}

	public function keepScheduled($numberOfMatches = 3) {
		$tournamentId = $this->data['id'];

		$stmt = $this->database->prepare("SELECT * FROM `matches` WHERE `state`='WAITING' AND `tournamentId`=?");
		$stmt->bindParam(1, $tournamentId);

		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}

		$realNumberOfMatches = $stmt->rowCount();

		if($realNumberOfMatches < $numberOfMatches) {
			$toCreate = $numberOfMatches - $realNumberOfMatches;

			for($i=0; $i<$toCreate; $i++) {
				$ladder = $this->getLadder();

				if(count($ladder) < 2) {
					throw new Exception("Not enough players to schedule a match.");
				}

					$a = $b = 0;
					while($a == $b) {
						$a = mt_rand(0, count($ladder)-1);
						$b = mt_rand(0, count($ladder)-1);
					}

					$this->scheduleMatch($ladder[$a]['id'], $ladder[$b]['id']);
			}
		}
	}

	public function clearSchedule() {
		$id = $this->data['id'];
		$stmt = $this->database->prepare("DELETE FROM `matches` WHERE `tournamentId`=? AND `state`='WAITING'");
		$stmt->bindParam(1, $id);
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
	}

	public function getUpcomingMatches($limit = 10) {
		$id = $this->data['id'];

		$stmt = $this->database->prepare("SELECT `matches`.*, `host`.`username` AS `hostName`, `guest`.`username` AS `guestName` FROM `matches` LEFT JOIN `users` AS `host` ON(`host`.`id`=`matches`.`hostUserId`) LEFT JOIN `users` AS `guest` ON (`guest`.`id`=`matches`.`guestUserId`) WHERE `tournamentId`=? AND `state`='WAITING' ORDER BY `id` ASC LIMIT 0,".(int)$limit);
		$stmt->bindParam(1, $id);

		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}

		$result = array();
		while ($row = $stmt->fetch()) {
			$result[] = $row;
		}
		return $result;
	}
	
	public function pollMatch() {
		$id = $this->data['id'];
		
		$stmt = $this->database->prepare("SELECT COUNT(*) AS `waiting` FROM `matches` WHERE `tournamentId`=? AND `state`='WAITING'");
		$stmt->bindParam(1, $id);
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
		
		$row = $stmt->fetch();
		$count = $row['waiting'];

        if(!$this->system->createNextMatch($count)) {
            return null;
        }

		$stmt2 = $this->database->prepare("SELECT * FROM `matches` WHERE `tournamentId`=? AND `state`='WAITING' ORDER BY `id` ASC LIMIT 0,1");
		$stmt2->bindParam(1, $id);
		
		if(!$stmt2->execute()) {
			throw new Exception("DB error");
		}
		
		if($stmt2->rowCount() != 1) {
			throw new Exception("Should have scheduled at least one match!");
		}
		
		$match = $stmt2->fetch();
		
		$userId = $match['hostUserId'];
		
		$stmt4 = $this->database->prepare("SELECT * FROM `bots` WHERE `userId`=? AND `tournamentId`=? AND `isActive`=1 ORDER BY `uploadTime` DESC LIMIT 0,1");
		$stmt4->bindParam(1, $userId);
		$stmt4->bindParam(2, $id);
		
		if(!$stmt4->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$hostBot = $stmt4->fetch();
		
		$userId = $match['guestUserId'];
		
		if(!$stmt4->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$guestBot = $stmt4->fetch();
		
		$now = time();
		
		$stmt3 = $this->database->prepare("UPDATE `matches` SET `state`='PLAYING', `startTime`=?, `hostBotId`=?, `guestBotId`=? WHERE `id`=?");
		$matchId = $match['id'];
		$stmt3->bindParam(1, $now);
		$stmt3->bindParam(2, $hostBot['id']);
		$stmt3->bindParam(3, $guestBot['id']);
		$stmt3->bindParam(4, $matchId);
		
		if(!$stmt3->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$match['hostBotId'] = $hostBot['id'];
		$match['guestBotId'] = $guestBot['id'];
		
		$match['mapUrl'] = $this->getMapUrl();
		$match['extras'] = $this->getExtras();
		
		return $match;
	}
	
	public function getPlayingMatch() {
		$tournamentId = $this->data['id'];
		
		$stmt = $this->database->prepare("SELECT matches.*, host.username AS hostName, guest.username AS guestName FROM matches, users AS host, users AS guest WHERE guest.id=matches.guestUserId AND host.id=matches.hostUserId AND `state`='PLAYING' AND tournamentId=? ORDER BY matches.id ASC LIMIT 0, 1");
		$stmt->bindParam(1, $tournamentId);
		
		if(!$stmt->execute())
			throw new Exception("DB: Query rrror");

		return $stmt->fetch();
	}

	public function deleteMatches() {
		$stmt = $this->database->prepare("DELETE FROM `matches` WHERE `tournamentId`=?");
		$stmt->bindParam(1, $this->data['id']);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}

	public function deleteBots() {
		$stmt = $this->database->prepare("DELETE FROM `bots` WHERE `tournamentId`=?");
		$stmt->bindParam(1, $this->data['id']);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}
	
	/**
	 * 
	 * @return \Nette\Database\Connection
	 */
	public function getDatabase() {
		return $this->database;
	}
	
	/**
	 * @return TournamentSystem
	 */
	public function getSystem() {
		return $this->system;
	}
	
	public function getLadderSnapshotData($forLastSeconds = self::LADDER_SNAPSHOT_LENGTH) {
		$now = time();
		$limit = $now - $forLastSeconds;
		$tournamentId = $this->data['id'];
		
		$stmt = $this->database->prepare("SELECT * FROM `ladder_snapshots` WHERE `time` > ? AND `tournamentId`=?");
		$stmt->bindParam(1, $limit);
		$stmt->bindParam(2, $tournamentId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: query error");
		}
		
		$snapshots = array();
		$users = array();
		
		while($row = $stmt->fetch()) {
			$sectionData = json_decode($row['ladder'], true);
			$snapshots[] = array('time' => $row['time'], 'ladder' => $sectionData);
			
			foreach($sectionData as $ladderRow) {
				$username = $ladderRow['username'];
				if(!in_array($username, $users)) {
					$users[] = $username;
				}

			}
		}
		
		$result = array(array("Time"));
		foreach($users as $user) {
			$result[0][] = $user;
		}
		
		foreach($snapshots as $snapshot) {
			$row = array((int)$snapshot['time']);
			$points = array();
			
			foreach($users as $user) {
				$points[$username] = 0;
			}
			
			foreach($snapshot['ladder'] as $ladderRow) {
				$points[$ladderRow['username']] = $ladderRow['pointsPerMatch'];
			}
			
			foreach($users as $user) {
				$row[] = $points[$user];
			}
			
			$result[] = $row;
		}

		return $result;
	}

	public function getAchievementsFromString($string) {
		$ar = explode(" ", $string);
		$result = array();

		foreach($ar as $name) {
			if($name == "") {
				continue;
			}
			$achievement = $this->achievements->getAchievement($name);
			$result[$achievement->getId()] = $achievement;
		}

		return $result;
	}

	public function getEarnedAchievements($earned) {
		$result = array();
		foreach($earned as $achievementName => $achievementData) {
			$achievement = $this->achievements->getAchievement($achievementName);
			$result[$achievement->getId()] = $achievement;
		}
		return $result;
	}

	public function getAchievements() {
		return $this->achievements;
	}
}
?>