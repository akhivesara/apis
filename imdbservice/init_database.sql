CREATE DATABASE  IF NOT EXISTS `nflxtakehome` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `nflxtakehome`;
-- MySQL dump 10.13  Distrib 8.0.13, for macos10.14 (x86_64)
--
-- Host: localhost    Database: nflxtakehome
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE USER 'nflxtakehome'@'localhost' IDENTIFIED BY 'nflxtakehome';

GRANT ALL PRIVILEGES ON nflxtakehome.* TO 'nflxtakehome'@'localhost';
--
-- Table structure for table `cast_title`
--

DROP TABLE IF EXISTS `cast_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `cast_title` (
  `tconst` varchar(45) NOT NULL,
  `nconst` varchar(20) NOT NULL,
  `category` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`tconst`,`nconst`),
  KEY `nId_idx` (`nconst`),
  CONSTRAINT `nId` FOREIGN KEY (`nconst`) REFERENCES `person` (`nconst`),
  CONSTRAINT `tId` FOREIGN KEY (`tconst`) REFERENCES `title` (`tconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `director_title`
--

DROP TABLE IF EXISTS `director_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `director_title` (
  `tconst` varchar(45) NOT NULL,
  `nconst` varchar(45) NOT NULL,
  PRIMARY KEY (`tconst`,`nconst`),
  KEY `tconst_idx` (`tconst`),
  KEY `nconst_idx` (`nconst`),
  CONSTRAINT `nconst` FOREIGN KEY (`nconst`) REFERENCES `person` (`nconst`),
  CONSTRAINT `tconst` FOREIGN KEY (`tconst`) REFERENCES `title` (`tconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `episode`
--

DROP TABLE IF EXISTS `episode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `episode` (
  `id` varchar(45) NOT NULL,
  `parentId` varchar(20) NOT NULL,
  `seasonNumber` int(11) DEFAULT NULL,
  `episodeNumber` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK0_idx` (`parentId`),
  CONSTRAINT `FK0` FOREIGN KEY (`parentId`) REFERENCES `title` (`tconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `genre_title`
--

DROP TABLE IF EXISTS `genre_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `genre_title` (
  `tconst` varchar(200) NOT NULL,
  `genre` varchar(500) NOT NULL,
  PRIMARY KEY (`tconst`,`genre`),
  CONSTRAINT `fk` FOREIGN KEY (`tconst`) REFERENCES `title` (`tconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `person` (
  `nconst` varchar(45) NOT NULL,
  `primaryName` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`nconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `rating` (
  `tconst` varchar(45) NOT NULL,
  `averageRating` decimal(10,2) DEFAULT NULL,
  `numVotes` int(11) DEFAULT NULL,
  PRIMARY KEY (`tconst`),
  UNIQUE KEY `tconst_UNIQUE` (`tconst`),
  KEY `tconst_idx` (`tconst`),
  CONSTRAINT `key` FOREIGN KEY (`tconst`) REFERENCES `title` (`tconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `title`
--

DROP TABLE IF EXISTS `title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `title` (
  `tconst` varchar(20) NOT NULL,
  `titleType` varchar(100) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `isAdult` int(1) DEFAULT NULL,
  `runtimeMinutes` int(11) DEFAULT NULL,
  PRIMARY KEY (`tconst`),
  KEY `titleType` (`titleType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `writer_title`
--

DROP TABLE IF EXISTS `writer_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `writer_title` (
  `tconst` varchar(45) NOT NULL,
  `nconst` varchar(45) NOT NULL,
  PRIMARY KEY (`tconst`,`nconst`),
  KEY `tconst_idx` (`tconst`),
  KEY `fk2_idx` (`nconst`),
  CONSTRAINT `fk1` FOREIGN KEY (`tconst`) REFERENCES `title` (`tconst`),
  CONSTRAINT `fk2` FOREIGN KEY (`nconst`) REFERENCES `person` (`nconst`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-01-10 12:59:21
