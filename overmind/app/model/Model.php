<?php

class Model {

	public static $extensions = array("exe", "dll", "jar");
	
	private $config = false;
	
	/**
	 * @var Nette\Database\Connection 
	 */
	private $database;
	
	private $tournaments = array();
	private $activeTournaments = array();
	private $archivedTournaments = array();
	private $tournamentsByCode = array();

	public function __construct(Nette\Database\Connection $database) {
		$this->database = $database;
        $this->getTournaments();
	}
	
	/**
	 * 
	 * @param type $tournamentId
	 * @return Tournament
	 * @throws Exception
	 */
	public function getTournament($tournamentId) {
		if(!isset($this->tournaments[$tournamentId])) {
			$stmt = $this->database->prepare("SELECT * FROM `tournaments` WHERE `id`=?");
			$stmt->bindParam(1, $tournamentId);
			
			if(!$stmt->execute()) {
				throw new Exception("DB: Query error");
			}
			
			if($stmt->rowCount() == 0) {
				return null;
			}

			$this->fetchTournament($stmt->fetch());
		}
		return $this->tournaments[$tournamentId];
	}

	public function getTournamentByCode($code) {
		if(!isset($this->tournamentsByCode[$code])) {
			$stmt = $this->database->prepare("SELECT * FROM `tournaments` WHERE `code`=?");
			$stmt->bindParam(1, $code);

			if(!$stmt->execute()) {
				throw new Exception("DB: Query error");
			}

			if($stmt->rowCount() == 0) {
				return null;
			}

			$this->fetchTournament($stmt->fetch());
		}
		return $this->tournamentsByCode[$code];
	}

	public function fetchTournament($data) {
		$tournament = new Tournament($this->database, $data);
		$this->tournaments[$data['id']] = $tournament;
		$this->tournamentsByCode[$data['code']] = $tournament;

		if(!$tournament->isArchived()) {
			$this->activeTournaments[$data['id']] = $tournament;
		} else {
			$this->archivedTournaments[$data['id']] = $tournament;
		}
	}
	
	public function createNewTournament(&$data) {
		$stmt = $this->database->prepare("INSERT INTO `tournaments` (`code`, `name`, `testStartTime`, `competitionStartTime`, `info`, `rules`, `mapUrl`, `extrasJson`) VALUES(?, ?, ?, ?, '{}', '{}', '', '{}')");
		$stmt->bindParam(1, $data['code']);
		$stmt->bindParam(2, $data['name']);
		$stmt->bindParam(3, $data['testStartTime']);
		$stmt->bindParam(4, $data['competitionStartTime']);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$data['id'] = $this->database->lastInsertId();
	}

	public function moveTournament($tournamentId, $offset = -1) {
	    if (!in_array($offset, [-1, 1])) throw new Exception("Offset cannot be other than -1 or 1");

        try {
            $this->database->beginTransaction();

            $stmt = $this->database->prepare("SELECT `id`, `priority` FROM `tournaments` WHERE 1 ORDER BY `priority` ASC");
            if(!$stmt->execute()) {
                throw new Exception("DB error");
            }

            $newPositions = [];
            $currentPosition = 0;
            $lastPosition = 0;
            for ($position=1; $row = $stmt->fetch(); $position++) {
                $newPositions[$position] = $row['id'];
                if ($row['id'] == $tournamentId) {
                    $currentPosition = $position;
                }
                $lastPosition = $position;
            }
            if ($currentPosition > 0) {
                $newPosition = $currentPosition + $offset;
                if ($newPosition > 0 && $newPosition <= $lastPosition) {
                    $tmp = $newPositions[$newPosition];
                    $newPositions[$newPosition] = $tournamentId;
                    $newPositions[$currentPosition] = $tmp;
                }
            }

            $id = 0;
            $position = 0;
            $stmt2 = $this->database->prepare("UPDATE `tournaments` SET `priority`=? WHERE `id`=?");
            $stmt2->bindParam(1, $position);
            $stmt2->bindParam(2, $id);
            foreach ($newPositions as $position => $tId) {
                $id = $tId;
                if (!$stmt2->execute()) {
                    throw new Exception("DB error");
                }
            }
            $this->database->commit();
        } catch(Exception $e) {
            $this->database->rollBack();
            throw $e;
        }

    }

	public function getTournaments() {
		$stmt = $this->database->prepare("SELECT * FROM `tournaments` WHERE 1 ORDER BY `priority` ASC");
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
		
		while($row = $stmt->fetch()) {
			if(isset($this->tournaments[$row['id']])) {
				continue;
			}
			$this->fetchTournament($row);
		}
		
		return $this->tournaments;
	}

	public function getActiveTournaments() {
		return $this->activeTournaments;
	}

	public function getArchivedTournaments() {
		return $this->archivedTournaments;
	}
	
	public function pollMatch($tournamentIds) {
		$allTournaments = $this->getTournaments();
		$tournaments = array();
		
		foreach($tournamentIds as $id) {
			$tournaments[] = $allTournaments[$id];
		}
		
		while(count($tournaments) > 0) {
			$key = rand(0, count($tournaments)-1);
			$tournament = $tournaments[$key];
			unset($tournaments[$key]);
			$tournaments = array_values($tournaments);
			
			$match = $tournament->pollMatch();
			
			if($match != null) {
				return $match;
			}
		}

		return null;
	}
	
	public function deleteTournament($tournamentId) {
		$tournament = $this->getTournament($tournamentId);

		if(isset($this->tournaments[$tournament->getId()])) {
			unset($this->tournaments[$tournament->getId()]);
			unset($this->activeTournaments[$tournament->getId()]);
			unset($this->archivedTournaments[$tournament->getId()]);
			unset($this->tournamentsByCode[$tournament->getCode()]);
		}
		$stmt = $this->database->prepare("DELETE FROM `tournaments` WHERE `id`=?");
		$stmt->bindParam(1, $tournamentId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}
	
	public function getConfig($key = null) {
		if($this->config == false) {
			$stmt = $this->database->prepare("SELECT * FROM `config`");
			
			if(!$stmt->execute())
				throw new Exception("DB error");
		
			$this->config = array();
			
			while($row = $stmt->fetch()) {
				$this->config[$row['key']] = $row['value'];
			}
		}
		
		if($key == null)
			return $this->config;
		return $this->config[$key];
	}
	
	public function setConfig($key, $value) {
		if($this->config != false) {
			$this->config[$key] = $value;
		}
		$stmt = $this->database->prepare("UPDATE `config` SET `value` = ? WHERE `key` = ?");
		$stmt->bindParam(1, $value);
		$stmt->bindParam(2, $key);
		
		if(!$stmt->execute())
			throw new Exception("DB error");
		
		if($stmt->rowCount() != 1) {
			$stmt = $this->database->prepare("INSERT INTO `config` (`key`, `value`) VALUES(?, ?)");
			$stmt->bindParam(1, $key);
			$stmt->bindParam(2, $value);
			
			if(!$stmt->execute())
				throw new Exception("DB error");
		}
	}
	
	public function getBotDetails($botId) {
		$stmt = $this->database->prepare("SELECT `bots`.*, `users`.`username` AS `username`, `users`.`id` AS `userId` FROM `bots` LEFT JOIN `users` ON (`users`.`id` = `bots`.`userId`) WHERE `bots`.`id`=?");
		$stmt->bindParam(1, $botId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		if($stmt->rowCount() != 1) {
			throw new Exception("Bot id {$botId} not found");
		}
		
		$bot = $stmt->fetch();
		
		// additional info
		$bot['url'] = BOTS_URL."/".$bot['fileName'];
		$bot['hash'] = md5_file(BOTS_PATH."/".$bot['fileName']);
		
		return $bot;
	}
	
	public function getMatchDetails($matchId) {
		$stmt = $this->database->prepare("SELECT matches.*, host.username AS hostName, guest.username AS guestName FROM matches, users AS host, users AS guest WHERE matches.hostUserId = host.id AND matches.guestUserId = guest.id AND matches.id=?");
		$stmt->bindParam(1, $matchId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		if($stmt->rowCount() != 1) {
			throw new Exception("DB: Match with ID {$matchId} not found.");
		}
		
		return $stmt->fetch();
	}
	
	public function setBotActive(Nette\Security\Identity $identity, $botId, $value = true) {
		$id = $identity->getId();
		
		$stmt = $this->database->prepare("UPDATE `bots` SET `isActive`=? WHERE `id`= ? AND `userId`=?");
		$stmt->bindParam(1, $value);
		$stmt->bindParam(2, $botId);
		$stmt->bindParam(3, $id);
		
		if(!$stmt->execute()) {
			throw new Exception("DB error");
		}
	}

	public function updateBotDetails($botId, $name, $comment) {
		$stmt = $this->database->prepare("UPDATE `bots` SET `name`=?, `comment`=? WHERE `id`=?");
		$stmt->bindParam(1, $name);
		$stmt->bindParam(2, $comment);
		$stmt->bindParam(3, $botId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public function handleMatchResult($data, Nette\Http\Request $request) {
		if (isset($data['botResults']) && is_array($data['botResults'])) {
			foreach ($data['botResults'] as &$bot) {
				$details = $this->getBotDetails($bot['botId']);
				$bot['userId'] = $details['userId'];
			}
		}
		
		$stmt = $this->database->prepare("SELECT * FROM `matches` WHERE `id`=?");
		$stmt->bindParam(1, $data['matchId']);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		if($stmt->rowCount() != 1) {
			throw new Exception("DB: Given match id {$data['matchId']} not found (row_count={$stmt->rowCount()})");
		}
		
		$match = $stmt->fetch();
		$tournament = $this->getTournament($match['tournamentId']);
		
		if($data['result'] == 'INVALID') {
			$stmt2 = $this->database->prepare("UPDATE `matches` SET `state`='WAITING', `result`=0, `startTime`=0, `endTime`=0 WHERE `id`=?");
			$stmt2->bindParam(1, $match['id']);
			
			if(!$stmt2->execute()) {
				throw new Exception("DB: Query error");
			}
			
			return;
		} else if($data['result'] != 'OK') {
			throw new Exception("Unknown match result '{$data['result']}'");
		}

		// RESULT IS OK
		if($match['hostUserId'] == $data['botResults'][0]['userId'] && $match['guestUserId'] == $data['botResults'][1]['userId']) {
			$hostMatch = $data['botResults'][0];
			$guestMatch = $data['botResults'][1];
		} else if($match['hostUserId'] == $data['botResults'][1]['userId'] && $match['guestUserId'] == $data['botResults'][0]['userId']) {
			$hostMatch = $data['botResults'][1];
			$guestMatch = $data['botResults'][0];
		} else {
			throw new Exception("Bot ids for result don't match the match info");
		}

		if(isset($hostMatch['achievements'])) {
			$hostAchievements = $tournament->getEarnedAchievements($hostMatch['achievements']);
		} else {
			$hostAchievements = array();
		}
		if(isset($guestMatch['achievements'])) {
			$guestAchievements = $tournament->getEarnedAchievements($guestMatch['achievements']);
		} else {
			$guestAchievements = array();
		}

		list($result, $hostPoints, $guestPoints) = $tournament->getPointsDependingOnAchievements(
			$hostAchievements, 
			$guestAchievements
		);

		$matchId = $match['id'];

		$now = time();
		
		$stmt3 = $this->database->prepare("UPDATE matches SET result = ?, state='FINISHED', `endTime`=?, `hostResult`=?, `guestResult`=?, `hostPoints`=?, `guestPoints`=? WHERE id = ?");
		$stmt3->bindParam(1, $result);
		$stmt3->bindParam(2, $now);
		$stmt3->bindParam(3, $hostMatch['matchResult']);
		$stmt3->bindParam(4, $guestMatch['matchResult']);
		$stmt3->bindParam(5, $hostPoints);
		$stmt3->bindParam(6, $guestPoints);
		$stmt3->bindParam(7, $matchId);

		if(!$stmt3->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$tournament->takeLadderSnapshot();

		foreach($hostAchievements as $hostAchievement) {
			$tournament->getAchievements()->addEarnedAchievement($match['hostBotId'], $match['id'], $hostAchievement);
		}
		foreach($guestAchievements as $guestAchievement) {
			$tournament->getAchievements()->addEarnedAchievement($match['guestBotId'], $match['id'], $guestAchievement);
		}
	}
	
	public function archiveTournamentAs($tournamentId, $newName, $shouldDeleteBots = false) {
		$tournament = $this->getTournament($tournamentId);

		if($tournament == null) {
			throw new Exception("Tournament with ID {$tournamentId} not found.");
		}

		$stmt = $this->database->prepare("INSERT INTO `tournaments` VALUES(?, NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1000)");
		$true = true;

		$info = $tournament->getInfoJson();
		$rules = $tournament->getRulesJson();

		$newCode = $tournament->getCode()."-archive";

		$stmt->bindParam(1, $newCode);
		$stmt->bindParam(2, $newName);
		$stmt->bindParam(3, $tournament->getTestStartTime());
		$stmt->bindParam(4, $tournament->getCompetitionStartTime());
		$stmt->bindParam(5, $info);
		$stmt->bindParam(6, $rules);
		$stmt->bindParam(7, $tournament->getHostStreamCode());
		$stmt->bindParam(8, $tournament->getGuestStreamCode());
		$stmt->bindParam(9, $tournament->getMapUrl());
		$stmt->bindParam(10, $tournament->getExtrasJson());
		$stmt->bindParam(11, $tournament->getSystemClassName());
		$stmt->bindParam(12, $true);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}

		$newId = $this->database->lastInsertId();

		$this->moveMatches($tournamentId, $newId);
		$this->moveBots($tournamentId, $newId);
		$this->moveLadderSnapshots($tournamentId, $newId);

		if(!$shouldDeleteBots) {
			$this->copyBots($newId, $tournamentId);
		}
	}

	public function moveMatches($fromId, $toId) {
		$stmt = $this->database->prepare("UPDATE `matches` SET `tournamentId`=? WHERE `tournamentId`=?");
		$stmt->bindParam(1, $toId);
		$stmt->bindParam(2, $fromId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}

	public function moveLadderSnapshots($fromId, $toId) {
		$sql = "UPDATE `ladder_snapshots` SET `tournamentId`=? WHERE `tournamentId`=?";
	 
 		$stmt = $this->database->prepare($sql);
 		$stmt->bindParam(1, $toId);
		$stmt->bindParam(2, $fromId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error with {$sql}");
		}
	}

	public function moveBots($fromId, $toId) {
		$stmt = $this->database->prepare("UPDATE `bots` SET `tournamentId`=? WHERE `tournamentId`=?");
		$stmt->bindParam(1, $toId);
		$stmt->bindParam(2, $fromId);

		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}

	public function copyBots($fromId, $toId) {
		$stmt = $this->database->prepare("SELECT * FROM `bots` WHERE `tournamentId`=?");
		$stmt->bindParam(1, $fromId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		while($row = $stmt->fetch()) {
			$stmt2 = $this->database->prepare("INSERT INTO `bots` (`userId`, `tournamentId`, `uploadTime`, `isActive`, `fileName`, `name`, `comment`) VALUES(?, ?, ?, ?, ?, ?, ?)");
			$stmt2->bindParam(1, $row['userId']);
			$stmt2->bindParam(2, $toId);
			$stmt2->bindParam(3, $row['uploadTime']);
			$stmt2->bindParam(4, $row['isActive']);
			$stmt2->bindParam(5, $row['fileName']);
			$stmt2->bindParam(6, $row['name']);
			$stmt2->bindPAram(7, $row['comment']);
			
			if(!$stmt2->execute()) {
				throw new Exception("DB: Query error");
			}
		}
	}
}
