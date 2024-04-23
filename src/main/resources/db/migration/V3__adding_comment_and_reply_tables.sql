CREATE TABLE `comment` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `content` text NOT NULL ,
                        `created_at` timestamp DEFAULT NULL,
                        `user_id` varchar(255) NOT NULL,
                        `post_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        CONSTRAINT `comment_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                        CONSTRAINT `comment_post_fk` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
);

CREATE TABLE `reply` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `content` text NOT NULL,
                           `created_at` timestamp DEFAULT NULL,
                           `user_id` varchar(255) NOT NULL,
                           `comment_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           CONSTRAINT `reply_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                           CONSTRAINT `reply_post_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE
);