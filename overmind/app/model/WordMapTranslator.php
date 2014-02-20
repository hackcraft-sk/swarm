<?php

/**
 * Description of Translator
 *
 * @author nixone
 */
class WordMapTranslator implements Nette\Localization\ITranslator {
	private $directory;
	private $wordMap = array();
	private $language;
	
	public function __construct($directory, $defaultLanguage="en") {
		$this->directory = __DIR__."/".$directory;
		$this->setLanguage($defaultLanguage);
	}
	
	public function setLanguage($language) {
		$file = $this->directory."/".$language.".txt";
		
		if(!file_exists($file)) {
			throw new Exception("Language file {$file} doesn't exist.");
		}
		
		$map = parse_ini_file($file);
		if($map === false) {
			throw new Exception("File {$file} is not valid language file (ini one-section format)");
		}
		
		$this->language = $language;
		$this->wordMap = $map;
	}
	
	public function translate($message, $count = null) {
		if(!isset($this->wordMap[$message])) {
			return $message;
		}
		return $this->wordMap[$message];
	}
	
	// TODO AUTOMATIC
	public function getLanguages() {
		return array("en" => "English");
	}
	
	public function getLanguage() {
		return $this->language;
	}
}
?>
