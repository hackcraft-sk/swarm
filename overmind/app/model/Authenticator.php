<?php

use Nette\Security,
	Nette\Utils\Strings;

/**
 * Users authenticator.
 */
class Authenticator extends Nette\Object implements Security\IAuthenticator
{
	/** @var Nette\Database\Connection */
	private $database;



	public function __construct(Nette\Database\Connection $database)
	{
		$this->database = $database;
	}



	/**
	 * Performs an authentication.
	 * @return Nette\Security\Identity
	 * @throws Nette\Security\AuthenticationException
	 */
	public function authenticate(array $credentials)
	{
		list($username, $password) = $credentials;
		$row = $this->database->table('users')->where('username', $username)->fetch();

		if (!$row) {
			throw new Security\AuthenticationException('The username is incorrect.', self::IDENTITY_NOT_FOUND);
		}

		if ($row->passwordHash !== $this->calculateHash($password, $row->passwordHash)) {
			throw new Security\AuthenticationException('The password is incorrect.', self::INVALID_CREDENTIAL);
		}

		unset($row->passwordHash);
		return new Security\Identity($row->id, null, $row->toArray());
	}



	/**
	 * Computes salted password hash.
	 * @param  string
	 * @return string
	 */
	public static function calculateHash($password, $salt = NULL)
	{
		if ($password === Strings::upper($password)) { // perhaps caps lock is on
			$password = Strings::lower($password);
		}
		return crypt($password, $salt ?: '$2a$07$' . Strings::random(22));
	}
	
	public function createAccount($username, $password) {
		$hash = self::calculateHash($password);
		
		$stmt = $this->database->prepare("INSERT INTO users (username, passwordHash) VALUES(?, ?)");
		$stmt->bindParam(1, $username);
		$stmt->bindParam(2, $hash);
		if(!$stmt->execute()) {
			throw new Exception("Execution failed");
		}
		
		if($stmt->rowCount() != 1) {
			throw new Exception("Account creation failed. Possible duplicates");
		}
	}
}
