<?php
/**
 * Description of JsonPresenter
 *
 * @author nixone
 */
class JsonPresenter extends BasePresenter {

	public function run(Nette\Application\Request $request) {
		$response = parent::run($request);

		if ($response instanceof \Nette\Application\Responses\JsonResponse) {

			$loggingDirectory = dirname(dirname(__DIR__))."/log/api";

			if ($request->isPost()) {
				$requestPayload = $request->getPost()["content"];
			} else {
				$requestPayload = $request->getParameters()["content"];
			}
			$responsePayload = $response->getPayload();

			$now = time();

			$files = array(
				$loggingDirectory."/by-time/".date("Y/Y-m/Y-m-d/Y-m-d-H/Y-m-d-H-i-s", $now)."-".$this->getView().".txt",
				$loggingDirectory."/by-type/".$this->getView()."/".date("Y/Y-m/Y-m-d/Y-m-d-H/Y-m-d-H-i-s", $now)."-".$this->getView().".txt",
			);

			$log = "";

			$log .= "Request payload:\n";
			$log .= json_encode(json_decode($requestPayload, true), JSON_PRETTY_PRINT)."\n\n";
			$log .= "Response:\n";
			$log .= json_encode($responsePayload, JSON_PRETTY_PRINT);

			foreach ($files as $file) {
				$dir = dirname($file);
				if (!file_exists($dir)) {
					mkdir($dir, 0777, true);
				}
				$fp = fopen($file, "w");
				fwrite($fp, $log);
				fclose($fp);
			}
		}
		return $response;
	}


	public function renderBotInfo($content) {
		try {
			$payload = json_decode($content, true);

			$id = (int)$payload['id'];

			$bot = $this->context->model->getBotDetails($id);
			
			$extensionParts = explode(".", $bot['url']);
			$extension = strtolower($extensionParts[count($extensionParts)-1]);

			switch($extension) {
				case "exe":
					$type = "CPP_CLIENT";
					break;
				case "dll":
					$type = "CPP_MODULE";
					break;
				case "jar":
					$type = "JAVA_CLIENT";
					break;
				default:
					throw new Exception("Unknown bot file extension '{$extension}'");
			}

			$displayName = $bot['name'] == $bot['username'] ? $bot['name'] : $bot['name']." (".$bot['username'].")";
			$displayName = iconv("utf-8", "ASCII//TRANSLIT", $displayName);

			$response = array(
				"name" => $displayName,
				"type" => $type,
				"botFileUrl" => $bot['url'],
				"botFileHash" => $bot['hash']
			);
		} catch(Exception $e) {
			$this->sendException($e);
		}

		if (isset($response)) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse($response));
		}
	}
	
	public function renderAssignMatch($content) {
		$payload = json_decode($content, true);

		try {
			$tournamentIds = $payload['tournamentIds'];
			
			if(!is_array($tournamentIds) || count($tournamentIds) == 0) {
				throw new Exception("Should provide tournament ids!");
			}
			
			$match = $this->context->model->pollMatch($tournamentIds);

			if($match == null) {
				$response = array(
					"matchId" => "NONE"
				);
			} else {
				$md5 = md5_file($match['mapUrl']);

				$response = array(
					"matchId" => $match['id'],
					"mapUrl" => $match['mapUrl'],
					"mapMd5" => $md5,
					"botIds" => array($match['hostBotId'], $match['guestBotId']),
					"videoStreamTargetBotId" => $match['hostBotId']
				);
			}
		} catch(Exception $e) {
			$this->sendException($e);
		}

		if (isset($response)) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse($response));
		}
	}
	
	public function renderPostMatchResult() {
		$payload = json_decode($this->getHttpRequest()->getPost("content"), true);
		
		$success = false;
		try {
			$this->context->model->handleMatchResult($payload, $this->getHttpRequest());
			$success = true;
		} catch(Exception $e) {
			$this->sendException($e);
		}
		
		if($success) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse(array("result" => "OK")));
		}
	}
	
	public function renderGetMatchInfo($content) {
		$payload = json_decode($content, true);
		
		try {
			$match = $this->context->model->getMatchDetails($payload['matchId']);
			$tournament = $this->context->model->getTournament($match['tournamentId']);

			// common attributes
			$response = array(
				"tournamentId" => $match['tournamentId'],
				"tournamentName" => $tournament->getName(),
				"bots" => array(
					array("name" => $match['hostName']),
					array("name" => $match['guestName'])
				)
			);

			if ($match['state'] == 'PLAYING' || $match['state'] == 'FINISHED') {
				$response['startTime'] = $match['startTime'];
			}

			if ($match['state'] == 'PLAYING') {
				$response['duration'] = 0;
				$response['state'] = 'NONE';

			} else if ($match['state'] == 'FINISHED') {
				$response['state'] = 'OK';
				$response['duration'] = $match['endTime'] - $match['startTime'];
				$response['bots'][0]['points'] = $match['hostResult'];
				$response['bots'][1]['points'] = $match['guestResult'];

			} else {
				$response['state'] = 'INVALID';
			}
		} catch(Exception $e) {
			$this->sendException($e);
		}

		if (isset($response)) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse($response));
		}
	}
	
	protected function sendException(Exception $e) {
		$this->sendError(get_class($e).": ({$e->getFile()}:{$e->getLine()}): {$e->getMessage()}", -1);
	}
	
	protected function sendError($message, $errorCode = 0) {
		$this->sendResponse(new Nette\Application\Responses\JsonResponse(array(
			"error" => array("message" => $message, "code" => $errorCode)
		)));
	}
}
?>
