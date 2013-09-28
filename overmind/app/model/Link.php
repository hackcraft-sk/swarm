<?php
/**
 * Description of Link
 *
 * @author nixone
 */
class Link {
	/**
	 * @var Nette\Database\Connection 
	 */
	private $database;
	
	public function __construct(Nette\Database\Connection $database) {
		$this->database = $database;
	}
	
	public function createNew($url, array $names, array $descriptions, $tournamentId = null) {
		$name = json_encode($names);
		$description = json_encode($descriptions);
		
		$stmt = $this->database->prepare("INSERT INTO `links` (`url`, `description`, `tournamentId`, `name`) VALUES(?, ?, ?, ?)");
		$stmt->bindParam(1, $url);
		$stmt->bindParam(2, $description);
		$stmt->bindParam(3, $tournamentId);
		$stmt->bindParam(4, $name);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
	}
	
	public function delete($id) {
		$stmt = $this->database->prepare("DELETE FROM `links` WHERE `id`=?");
		$stmt->bindParam(1, $id);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		if($stmt->rowCount() != 1) {
			throw new Exception("DB: Link not deleted, it doesn't exist.");
		}
	}
	
	public function getAll($tournamentId) {
		$stmt = $this->database->prepare("SELECT * FROM `links` WHERE `tournamentId` IS NULL OR `tournamentId`=? ORDER BY `name` ASC");
		$stmt->bindParam(1, $tournamentId);
		
		if(!$stmt->execute()) {
			throw new Exception("DB: Query error");
		}
		
		$result = array();
		
		while($row = $stmt->fetch()) {
			$row['descriptions'] = json_decode($row['description'], true);
			$row['names'] = json_decode($row['name'], true);
			
			$result[$row['id']] = $row;
		}
		
		return $result;
	}
}
?>
