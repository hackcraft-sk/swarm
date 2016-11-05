<?php
use \Firebase\JWT\JWT;
use Nette\Mail\Message;
use Nette\Mail\SendmailMailer;

/**
 * Created by PhpStorm.
 * User: marti
 * Date: 2016-11-05
 * Time: 13:52
 */
class EmailVerification {

    private $encryptionKey;

    public function __construct($encryptionKey) {
        $this->encryptionKey = $encryptionKey;
    }

    public function createToken($email) {
        return JWT::encode(array('email' => $email), $this->encryptionKey);
    }

    public function sendLink($email, $link) {
        $mail = new Message;
        $mail->setFrom('My Life for AI <info@mylifeforai.com>')
            ->addTo($email)
            ->setSubject('My Life for AI Account Email Verification')
            ->setBody("Hi!\n\nVerify your account clicking on this link:\n\n".$link."\n\nSee you!\n\nMLA Team");

        $mailer = new SendmailMailer;
        $mailer->send($mail);
    }

    public function decodeEmail($token) {
        $payload = JWT::decode($token, $this->encryptionKey, array("HS256"));
        return $payload->email;
    }
}