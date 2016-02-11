-- phpMyAdmin SQL Dump
-- version 4.0.10.12
-- http://www.phpmyadmin.net
--
-- Host: localhost:3306
-- Generation Time: Feb 11, 2016 at 07:00 AM
-- Server version: 5.1.73-14.12-log
-- PHP Version: 5.5.9-1ubuntu4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `h3thy07y`
--

-- --------------------------------------------------------

--
-- Table structure for table `achievements`
--

CREATE TABLE IF NOT EXISTS `achievements` (
  `id` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `isVisible` tinyint(1) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `achievements_earned`
--

CREATE TABLE IF NOT EXISTS `achievements_earned` (
  `botId` int(10) unsigned NOT NULL,
  `matchId` int(10) unsigned NOT NULL,
  `achievementId` varchar(64) NOT NULL,
  PRIMARY KEY (`botId`,`achievementId`,`matchId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `achievements_in_tournaments`
--

CREATE TABLE IF NOT EXISTS `achievements_in_tournaments` (
  `achievementId` varchar(64) NOT NULL,
  `tournamentId` int(10) unsigned NOT NULL,
  PRIMARY KEY (`achievementId`,`tournamentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bots`
--

CREATE TABLE IF NOT EXISTS `bots` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `tournamentId` int(10) unsigned NOT NULL,
  `uploadTime` bigint(20) unsigned NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT '0',
  `fileName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `comment` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `tournamentId` (`tournamentId`),
  KEY `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE IF NOT EXISTS `config` (
  `key` varchar(255) CHARACTER SET latin1 NOT NULL,
  `value` varchar(255) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ladder_snapshots`
--

CREATE TABLE IF NOT EXISTS `ladder_snapshots` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tournamentId` int(10) unsigned NOT NULL,
  `time` bigint(20) unsigned NOT NULL,
  `ladder` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `links`
--

CREATE TABLE IF NOT EXISTS `links` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tournamentId` int(10) unsigned DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tournament` (`tournamentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `matches`
--

CREATE TABLE IF NOT EXISTS `matches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tournamentId` int(10) unsigned NOT NULL,
  `state` enum('WAITING','PLAYING','FINISHED') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'WAITING',
  `hostUserId` int(11) NOT NULL,
  `guestUserId` int(11) NOT NULL,
  `result` int(11) NOT NULL DEFAULT '0',
  `startTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `endTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `hostBotId` int(10) unsigned NOT NULL,
  `guestBotId` int(10) unsigned NOT NULL,
  `hostResult` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `guestResult` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `hostPoints` int(11) NOT NULL DEFAULT '0',
  `guestPoints` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `tournament` (`tournamentId`),
  KEY `hostUser` (`hostUserId`),
  KEY `guestUser` (`guestUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `tournaments`
--

CREATE TABLE IF NOT EXISTS `tournaments` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `testStartTime` bigint(20) unsigned NOT NULL,
  `competitionStartTime` bigint(20) unsigned NOT NULL,
  `info` text COLLATE utf8_unicode_ci NOT NULL,
  `rules` text COLLATE utf8_unicode_ci NOT NULL,
  `hostStreamCode` text COLLATE utf8_unicode_ci NOT NULL,
  `guestStreamCode` text COLLATE utf8_unicode_ci NOT NULL,
  `mapUrl` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `extrasJson` text COLLATE utf8_unicode_ci NOT NULL,
  `system` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'NormalTournamentSystem',
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2 ;

--
-- Dumping data for table `tournaments`
--

INSERT INTO `tournaments` (`id`, `name`, `testStartTime`, `competitionStartTime`, `info`, `rules`, `hostStreamCode`, `guestStreamCode`, `mapUrl`, `extrasJson`, `system`, `archived`) VALUES
(1, 'Tournament', 1455170341, 1455170342, '{}', '{}', '', '', '', '', 'NormalTournamentSystem', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` text CHARACTER SET latin1,
  `passwordHash` text CHARACTER SET latin1,
  `isAdmin` int(11) NOT NULL DEFAULT '0',
  `competitive` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bots`
--
ALTER TABLE `bots`
  ADD CONSTRAINT `bots_ibfk_5` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bots_ibfk_6` FOREIGN KEY (`tournamentId`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `links`
--
ALTER TABLE `links`
  ADD CONSTRAINT `links_ibfk_1` FOREIGN KEY (`tournamentId`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `matches`
--
ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_4` FOREIGN KEY (`tournamentId`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `matches_ibfk_5` FOREIGN KEY (`hostUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `matches_ibfk_6` FOREIGN KEY (`guestUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
