<?php

/**
 * Created by PhpStorm.
 * User: marti
 * Date: 2016-11-04
 * Time: 20:46
 */
class Pages {

    private $database;

    public function __construct(Nette\Database\Connection $database) {
        $this->database = $database;
    }

    public function getContent($slug) {
        $stmt = $this->database->prepare("SELECT * FROM `pages` WHERE `slug`=?");
        $stmt->bindParam(1, $slug);

        if(!$stmt->execute()) {
            throw new Exception("Couldnt execute search for page based on slug");
        }

        $result = $stmt->fetch();
        if (!is_object($result)) {
            return false;
        }
        return $result->content;
    }

    public function create($slug, $content) {
        $stmt = $this->database->prepare("INSERT INTO `pages` (`slug`, `content`) VALUES(?, ?)");
        $stmt->bindParam(1, $slug);
        $stmt->bindParam(2, $content);

        if (!$stmt->execute()) {
            throw new Exception("Couldnt execute create page query");
        }
    }

    public function update($originalSlug, $slug, $content) {
        $stmt = $this->database->prepare("UPDATE `pages` SET `slug`=?, `content`=? WHERE `slug`=?");
        $stmt->bindParam(1, $slug);
        $stmt->bindParam(2, $content);
        $stmt->bindParam(3, $originalSlug);

        if (!$stmt->execute()) {
            throw new Exception("Couldnt execute create page query");
        }

        return true;
    }

    public function delete($slug) {
        $stmt = $this->database->prepare("DELETE FROM `pages` WHERE `slug`=?");
        $stmt->bindParam(1, $slug);

        if (!$stmt->execute()) {
            throw new Exception("Couldnt execute delete page query");
        }

        return true;
    }

    public function getAll() {
        $stmt = $this->database->prepare("SELECT * FROM `pages` ORDER BY `slug` ASC");

        if (!$stmt->execute()) {
            throw new Exception("Couldnt execute select pages query");
        }

        $result = array();
        while ($row = $stmt->fetch()) {
            $result[] = $row;
        }

        return $result;
    }
}