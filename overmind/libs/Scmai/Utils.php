<?php
namespace Scmai\Utils;

function getCurrentTimezoneAbbr() {
	$dateTime = new \DateTime(); 
	$dateTime->setTimeZone(new \DateTimeZone(\date_default_timezone_get())); 
	return $dateTime->format('T'); 
}