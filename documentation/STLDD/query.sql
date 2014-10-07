
CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `session` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_group_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `week` int(11) NOT NULL,
  `total_time` int(11) NOT NULL,
  `signed` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_group_id` (`user_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `report_times` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) NOT NULL,
  `SDP_U` int(11) NOT NULL,
  `SDP_I` int(11) NOT NULL,
  `SDP_F` int(11) NOT NULL,
  `SDP_O` int(11) NOT NULL,
  `SRS_U` int(11) NOT NULL,
  `SRS_I` int(11) NOT NULL,
  `SRS_F` int(11) NOT NULL,
  `SRS_O` int(11) NOT NULL,
  `SVVS_U` int(11) NOT NULL,
  `SVVS_I` int(11) NOT NULL,
  `SVVS_F` int(11) NOT NULL,
  `SVVS_O` int(11) NOT NULL,
  `STLDD_U` int(11) NOT NULL,
  `STLDD_I` int(11) NOT NULL,
  `STLDD_F` int(11) NOT NULL,
  `STLDD_O` int(11) NOT NULL,
  `SVVI_U` int(11) NOT NULL,
  `SVVI_I` int(11) NOT NULL,
  `SVVI_F` int(11) NOT NULL,
  `SVVI_O` int(11) NOT NULL,
  `SDDD_U` int(11) NOT NULL,
  `SDDD_I` int(11) NOT NULL,
  `SDDD_F` int(11) NOT NULL,
  `SDDD_O` int(11) NOT NULL,
  `SVVR_U` int(11) NOT NULL,
  `SVVR_I` int(11) NOT NULL,
  `SVVR_F` int(11) NOT NULL,
  `SVVR_O` int(11) NOT NULL,
  `SSD_U` int(11) NOT NULL,
  `SSD_I` int(11) NOT NULL,
  `SSD_F` int(11) NOT NULL,
  `SSD_O` int(11) NOT NULL,
  `slutrapport_U` int(11) NOT NULL,
  `slutrapport_I` int(11) NOT NULL,
  `slutrapport_F` int(11) NOT NULL,
  `slutrapport_O` int(11) NOT NULL,
  `funktionstest` int(11) NOT NULL,
  `systemtest` int(11) NOT NULL,
  `regressionstest` int(11) NOT NULL,
  `meeting` int(11) NOT NULL,
  `lecture` int(11) NOT NULL,
  `excersice` int(11) NOT NULL,
  `terminal` int(11) NOT NULL,
  `study` int(11) NOT NULL,
  `other` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `report_id` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(10) NOT NULL,
  `password` varchar(10) NOT NULL,
  `is_admin` tinyint(4) NOT NULL,
  `is_project_leader` tinyint(4) NOT NULL,
  `is_logged_in` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `user_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


ALTER TABLE `log`
  ADD CONSTRAINT `log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`);

ALTER TABLE `report_times`
  ADD CONSTRAINT `report_times_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`);

ALTER TABLE `user_group`
  ADD CONSTRAINT `user_group_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  ADD CONSTRAINT `user_group_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
