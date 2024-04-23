CREATE TABLE `media` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `aws_id` varchar(255) NOT NULL ,
                        `name` varchar(255) DEFAULT NULL,
                        `type` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `post_media` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `post_id` bigint NOT NULL,
                              `media_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `Unique` (`post_id`,`media_id`),
                              CONSTRAINT `post_media_media_fk` FOREIGN KEY (`media_id`) REFERENCES `media` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `post_media_post_fk` FOREIGN KEY (`post_id`) REFERENCES `media` (`id`) ON DELETE CASCADE
);