<?php
define('URL_BASE', 'http'.($_SERVER['HTTPS'] ? 's' : '').'://'.$_SERVER['SERVER_NAME'].':'.$_SERVER['SERVER_PORT']);
define('PATH_BASE', dirname(__DIR__));

define('BOTS_PATH', PATH_BASE . '/www/uploaded_bots');
define('BOTS_URL', URL_BASE . '/uploaded_bots');

define('REPLAYS_PATH', PATH_BASE . '/www/replays');
define('REPLAYS_URL', URL_BASE . '/replays');

define('ACHIEVEMENTS_PATH', PATH_BASE . '/www/achievements');
define('ACHIEVEMENTS_URL', URL_BASE . '/achievements');