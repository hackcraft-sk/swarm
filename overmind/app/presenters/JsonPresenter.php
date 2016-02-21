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
		} catch(Exception $e) {
			$this->sendException($e);
		}

		$displayName = $bot['name'] == $bot['username'] ? $bot['name'] : $bot['name']." (".$bot['username'].")";
		$displayName = iconv("utf-8", "ASCII//TRANSLIT", $displayName);

		$this->sendResponse(new Nette\Application\Responses\JsonResponse(array(
			"name" => $displayName,
			"type" => $type,
			"botFileUrl" => $bot['url'],
			"botFileHash" => $bot['hash']
		)));
	}
	
	public function renderAssignMatch($content) {
		$payload = json_decode($content, true);
		
		try {
			$tournamentIds = $payload['tournamentIds'];
			
			if(!is_array($tournamentIds) || count($tournamentIds) == 0) {
				throw new Exception("Should provide tournament ids!");
			}
			
			$match = $this->context->model->pollMatch($tournamentIds);
		} catch(Exception $e) {
			$this->sendException($e);
		}
			
		if($match == null) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse(array(
				"matchId" => "NONE"
			)));
		} else {
			$md5 = md5_file($match['mapUrl']);

			$payload = array(
				"matchId" => $match['id'],
				"mapUrl" => $match['mapUrl'],
				"mapMd5" => $md5,
				"botIds" => array($match['hostBotId'], $match['guestBotId'])
			);
			
			if(count($match['extras']) > 0) {
				$payload['extras'] = $match['extras'];
			}
			
			$this->sendResponse(new Nette\Application\Responses\JsonResponse($payload));
		}
	}
	
	public function renderPostMatchResult($content) {
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
			
			$resultPayload = array(
				"tournamentId" => $match['tournamentId'],
				"tournamentName" => $this->context->model->getTournament($match['tournamentId'])->getName(),
				"result" => "NONE",
				"bots" => array(
					array("name" => $match['hostName']),
					array("name" => $match['guestName'])
				)
			);
			
			if($match['state'] == 'FINISHED') {
				$resultPayload['result'] = "OK";
				$resultPayload['bots'][0]['result'] = $match['hostResult'];
				$resultPayload['bots'][1]['result'] = $match['guestResult'];
			}
			
			
		} catch(Exception $e) {
			$this->sendException($e);
		}
		
		if(isset($resultPayload)) {
			$this->sendResponse(new Nette\Application\Responses\JsonResponse($resultPayload));
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
