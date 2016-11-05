<?php

/**
 * Created by PhpStorm.
 * User: marti
 * Date: 2016-11-05
 * Time: 9:05
 */
class Captcha
{
    private $recaptcha;

    public function __construct($secret) {
        $this->recaptcha = new \ReCaptcha\ReCaptcha($secret);
    }

    public function getRecaptcha() {
        return $this->recaptcha;
    }
}