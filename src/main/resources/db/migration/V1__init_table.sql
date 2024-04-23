
CREATE TABLE `user` (
                        `id` varchar(255) NOT NULL,
                        `email` varchar(255) NOT NULL ,
                        `username` varchar(255) NOT NULL ,
                        `is_admin` boolean DEFAULT 0,
                        UNIQUE KEY `username` (`username`),
                        UNIQUE KEY `email` (`email`),
                        PRIMARY KEY (`id`)
);

CREATE TABLE `sn_groups` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) NOT NULL,
                             `admin_id` varchar(255) NOT NULL,
                             PRIMARY KEY (`id`)
);
CREATE TABLE `location` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `address` varchar(255) NOT NULL,
                            `city` varchar(255) NOT NULL,
                            `country` varchar(255) NOT NULL,
                            `postcode` int DEFAULT NULL,
                            `x_coordinates` double DEFAULT NULL,
                            `y_coordinates` double DEFAULT NULL,
                            PRIMARY KEY (`id`)
);
CREATE TABLE `event` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `description` varchar(255) DEFAULT NULL,
                         `event_time` datetime(6) NOT NULL,
                         `name` varchar(255) NOT NULL,
                         `group_id` bigint NOT NULL,
                         `created_by` varchar(255) NOT NULL,
                         `location_id` bigint NOT NULL,
                         PRIMARY KEY (`id`),
                         CONSTRAINT `event_location_fk` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
                         CONSTRAINT `event_group_fk` FOREIGN KEY (`group_id`) REFERENCES `sn_groups` (`id`) ON DELETE CASCADE
);

CREATE TABLE `post` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `content` text NOT NULL,
                        `created_at` timestamp DEFAULT NULL,
                        `deleted_at` timestamp DEFAULT NULL,
                        `user_id` varchar(255) NOT NULL,
                        `group_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        CONSTRAINT `post_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                        CONSTRAINT `post_group_fk` FOREIGN KEY (`group_id`) REFERENCES `sn_groups` (`id`) ON DELETE CASCADE
);

CREATE TABLE `event_respond` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `respond_type` enum('ACCEPTED','REJECTED') NOT NULL,
                                 `event_id` bigint NOT NULL,
                                 `user_id` varchar(255) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `Unique` (`event_id`,`user_id`),
                                 CONSTRAINT `event_respond_event_fk` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `event_respond_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);



CREATE TABLE `friend_request` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `status` enum('APPROVED','REJECTED','PENDING') NOT NULL,
                                  `receiver_id` varchar(255) NOT NULL,
                                  `sender_id` varchar(255) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `fr_user1_fk` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `fr_user2_fk` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);

CREATE TABLE `friendship` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `friends_since` timestamp DEFAULT NULL,
                              `user1_id` varchar(255) NOT NULL,
                              `user2_id` varchar(255) NOT NULL,
                              PRIMARY KEY (`id`),
                              CONSTRAINT `friendship_user1_fk` FOREIGN KEY (`user1_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `friendship_user2_fk` FOREIGN KEY (`user2_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);

CREATE TABLE `group_membership` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `member_since` timestamp DEFAULT NULL,
                                    `group_id` bigint NOT NULL,
                                    `user_id` varchar(255) NOT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `Unique` (`group_id`,`user_id`),
                                    CONSTRAINT `membership_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `membership_group_fk` FOREIGN KEY (`group_id`) REFERENCES `sn_groups` (`id`) ON DELETE CASCADE
);


CREATE TABLE `hidden_from` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `post_id` bigint NOT NULL,
                               `user_id` varchar(255) NOT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `Unique` (`post_id`,`user_id`),
                               CONSTRAINT `hidden_from_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `hidden_from_post_fk` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
);



CREATE TABLE `join_group_request` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `respond_type` enum('ACCEPTED','REJECTED', 'PENDING', 'PENDING_INVITATION_ADMIN', 'PENDING_INVITATION_FRIEND', 'WAITING_FOR_ADMIN_APPROVAL') DEFAULT NULL,
                                      `group_id` bigint NOT NULL,
                                      `user_id` varchar(255) NOT NULL,
                                      PRIMARY KEY (`id`),
                                      CONSTRAINT `join_group_request_group_fk` FOREIGN KEY (`group_id`) REFERENCES `sn_groups` (`id`) ON DELETE CASCADE,
                                      CONSTRAINT `join_group_request_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);



CREATE TABLE `notification_settings` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `turned_off_until` datetime(6) DEFAULT NULL,
                                         `type` enum('TEMPORARILY','PERMANENTLY') NOT NULL,
                                         `user_id` varchar(255) NOT NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `Unique` (`user_id`),
                                         CONSTRAINT `notification_settings_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);







