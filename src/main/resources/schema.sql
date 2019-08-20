CREATE DATABASE IF NOT EXISTS `getvideobot`;
USE `getvideobot`;

CREATE TABLE IF NOT EXISTS `tweet_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `mention_id` varchar(255) NOT NULL,
  `media_tweet_id` varchar(255) DEFAULT NULL,
  `media_url` varchar(255) DEFAULT NULL,
  `media_tweet_user` varchar(20) DEFAULT NULL,
  `media_tweet_text` varchar(300) DEFAULT NULL,
  `time_saved` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`mention_id`)
) ENGINE=InnoDB;
