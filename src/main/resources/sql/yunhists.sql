-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Mar 15, 2023 at 11:33 PM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `yunhists`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `zh_name` varchar(255) NOT NULL,
  `en_name` varchar(255) NOT NULL,
  `cat_theses` int(3) NOT NULL,
  `cat_subcats` int(2) NOT NULL DEFAULT 0,
  `operator` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `categoryLinks`
--

CREATE TABLE `categoryLinks` (
  `id` int(11) NOT NULL,
  `cat_from` int(11) NOT NULL,
  `cat_to` int(11) NOT NULL,
  `cat_to_zhName` varchar(255) NOT NULL,
  `cat_to_enName` varchar(255) NOT NULL,
  `cat_type` int(1) NOT NULL COMMENT '0=thesisToCat,1=catToCat',
  `operator` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `delThesis`
--

CREATE TABLE `delThesis` (
  `id` int(11) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `publication` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `publisher` varchar(255) NOT NULL,
  `year` year(4) DEFAULT NULL,
  `volume` int(4) DEFAULT NULL,
  `issue` varchar(5) NOT NULL,
  `pages` varchar(20) NOT NULL,
  `doi` varchar(255) NOT NULL,
  `isbn` varchar(17) NOT NULL,
  `online_publisher` varchar(255) NOT NULL,
  `online_publish_url` varchar(255) NOT NULL,
  `type` int(1) NOT NULL COMMENT '0=journal,1=collection,2=chapter',
  `copyright_status` int(1) NOT NULL COMMENT '0=AllRightsReserved,1=OpenAccess,2=PublicDomain',
  `file_name` varchar(255) NOT NULL,
  `uploader` int(11) NOT NULL,
  `approver` int(11) NOT NULL,
  `approve_time` timestamp NULL DEFAULT NULL,
  `del_operator` int(11) NOT NULL,
  `del_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `emailAuth`
--

CREATE TABLE `emailAuth` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `verification_code` varchar(6) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `emailTimer`
--

CREATE TABLE `emailTimer` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `action` varchar(50) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `upload`
--

CREATE TABLE `upload` (
  `id` int(11) NOT NULL,
  `author` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `publication` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `publisher` varchar(255) NOT NULL,
  `year` year(4) DEFAULT NULL,
  `volume` int(4) DEFAULT NULL,
  `issue` varchar(5) NOT NULL,
  `pages` varchar(20) NOT NULL,
  `doi` varchar(255) NOT NULL,
  `isbn` varchar(17) NOT NULL,
  `online_publisher` varchar(255) NOT NULL,
  `online_publish_url` varchar(255) NOT NULL,
  `type` int(1) NOT NULL COMMENT '0=journal,1=collection,2=chapter,3=newspaper',
  `copyright_status` int(1) NOT NULL COMMENT '0=AllRightsReserved,1=OpenAccess,2=PublicDomain',
  `file_name` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `new_category` varchar(255) NOT NULL,
  `uploader` int(11) NOT NULL,
  `upload_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` int(1) NOT NULL COMMENT '0=unapproved,1=approved,2=failapproved',
  `approver` int(11) DEFAULT NULL,
  `approve_time` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `thesis`
--

CREATE TABLE `thesis` (
  `id` int(11) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `publication` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `publisher` varchar(255) NOT NULL,
  `year` year(4) DEFAULT NULL,
  `volume` int(4) DEFAULT NULL,
  `issue` varchar(5) NOT NULL,
  `pages` varchar(20) NOT NULL,
  `doi` varchar(255) NOT NULL,
  `isbn` varchar(17) NOT NULL,
  `online_publisher` varchar(255) NOT NULL,
  `online_publish_url` varchar(255) NOT NULL,
  `type` int(1) NOT NULL COMMENT '0=journal,1=collection,2=chapter,3=newspaper',
  `copyright_status` int(1) NOT NULL COMMENT '0=AllRightsReserved,1=OpenAccess,2=PublicDomain',
  `file_name` varchar(255) NOT NULL,
  `uploader` int(11) NOT NULL,
  `approver` int(11) NOT NULL,
  `approve_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `user_rights` int(11) NOT NULL DEFAULT 0 COMMENT '0=user,1=admin',
  `lang` varchar(3) NOT NULL DEFAULT 'zh',
  `points` int(5) NOT NULL DEFAULT 0,
  `today_download` int(11) NOT NULL DEFAULT 0,
  `send_email` varchar(3) NOT NULL DEFAULT 'ON' COMMENT 'ON, OFF',
  `register_type` int(11) NOT NULL DEFAULT 0 COMMENT '0=email,1=google',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `zh_name` (`zh_name`),
  ADD UNIQUE KEY `en_name` (`en_name`),
  ADD KEY `category_operator` (`operator`);

--
-- Indexes for table `categoryLinks`
--
ALTER TABLE `categoryLinks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `catlink_to_cat` (`cat_to`),
  ADD KEY `catlink_operator` (`operator`);

--
-- Indexes for table `delThesis`
--
ALTER TABLE `delThesis`
  ADD PRIMARY KEY (`id`),
  ADD KEY `del_thesis_uploader` (`uploader`),
  ADD KEY `del_thesis_approver` (`approver`);

--
-- Indexes for table `emailAuth`
--
ALTER TABLE `emailAuth`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `emailTimer`
--
ALTER TABLE `emailTimer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `upload`
--
ALTER TABLE `upload`
  ADD PRIMARY KEY (`id`),
  ADD KEY `upload_uploader` (`uploader`),
  ADD KEY `upload_approver` (`approver`);

--
-- Indexes for table `thesis`
--
ALTER TABLE `thesis`
  ADD PRIMARY KEY (`id`),
  ADD KEY `thesis_uploader` (`uploader`),
  ADD KEY `thesis_approver` (`approver`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT for table `categoryLinks`
--
ALTER TABLE `categoryLinks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=278;

--
-- AUTO_INCREMENT for table `delThesis`
--
ALTER TABLE `delThesis`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `emailAuth`
--
ALTER TABLE `emailAuth`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=324;

--
-- AUTO_INCREMENT for table `emailTimer`
--
ALTER TABLE `emailTimer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=277;

--
-- AUTO_INCREMENT for table `upload`
--
ALTER TABLE `upload`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `thesis`
--
ALTER TABLE `thesis`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=361;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `category`
--
ALTER TABLE `category`
  ADD CONSTRAINT `category_operator` FOREIGN KEY (`operator`) REFERENCES `user` (`id`);

--
-- Constraints for table `categoryLinks`
--
ALTER TABLE `categoryLinks`
  ADD CONSTRAINT `catlink_operator` FOREIGN KEY (`operator`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `catlink_to_cat` FOREIGN KEY (`cat_to`) REFERENCES `category` (`id`);

--
-- Constraints for table `delThesis`
--
ALTER TABLE `delThesis`
  ADD CONSTRAINT `del_thesis_approver` FOREIGN KEY (`approver`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `del_thesis_uploader` FOREIGN KEY (`uploader`) REFERENCES `user` (`id`);

--
-- Constraints for table `upload`
--
ALTER TABLE `upload`
  ADD CONSTRAINT `upload_approver` FOREIGN KEY (`approver`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `upload_uploader` FOREIGN KEY (`uploader`) REFERENCES `user` (`id`);

--
-- Constraints for table `thesis`
--
ALTER TABLE `thesis`
  ADD CONSTRAINT `thesis_approver` FOREIGN KEY (`approver`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `thesis_uploader` FOREIGN KEY (`uploader`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
