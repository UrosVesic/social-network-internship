DROP TABLE `reply`;
ALTER TABLE `comment` ADD COLUMN `parent_id` BIGINT NULL AFTER `created_at`;
ALTER TABLE `comment` ADD CONSTRAINT `comment_parent_fk` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ;