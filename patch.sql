ALTER TABLE user ADD COLUMN `native_lang_id` VARCHAR(2) AFTER `enabled`;

ALTER TABLE translation ADD COLUMN `user_id` BIGINT AFTER `translator_id`;
ALTER TABLE translation ADD COLUMN `pre_text` VARCHAR(128) NULL DEFAULT NULL;
ALTER TABLE translation ADD COLUMN `post_text` VARCHAR(128) NULL DEFAULT NULL;

CREATE  TABLE IF NOT EXISTS `user_lang` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NOT NULL ,
  `lang_id` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`id`));

