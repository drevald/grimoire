SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `transition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `transition` ;

CREATE  TABLE IF NOT EXISTS `transition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `event` VARCHAR(32) NULL DEFAULT NULL ,
  `src_status` VARCHAR(32) NULL DEFAULT NULL ,
  `dest_status` VARCHAR(32) NULL DEFAULT NULL ,
  `handler_name` VARCHAR(32) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
AUTO_INCREMENT = 17
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `lang`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `lang` ;

CREATE  TABLE IF NOT EXISTS `lang` (
  `id` VARCHAR(2) NULL DEFAULT NULL ,
  `name` VARCHAR(16) NULL DEFAULT NULL ,
  `encodings` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user` ;

CREATE  TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(32) NOT NULL ,
  `password` VARCHAR(32) NOT NULL ,
  `role` VARCHAR(8) NOT NULL DEFAULT 'USER' ,
  `enabled` TINYINT(1)  NOT NULL DEFAULT TRUE ,
  `native_lang_id` VARCHAR(2),
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) )
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `text`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `text` ;

CREATE  TABLE IF NOT EXISTS `text` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `orig_path` VARCHAR(128) NOT NULL ,
  `utf8_path` VARCHAR(128) NOT NULL ,
  `encoding` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id`) )
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dict`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dict` ;

CREATE  TABLE IF NOT EXISTS `dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(128) NULL DEFAULT NULL ,
  `orig_doc` LONGBLOB NULL DEFAULT NULL ,
  `utf8_text` LONGTEXT NULL DEFAULT NULL ,
  `status` VARCHAR(32) NULL DEFAULT NULL ,
  `encoding` VARCHAR(32) NULL DEFAULT NULL ,
  `preview` BLOB NULL DEFAULT NULL ,
  `lang_id` VARCHAR(2) NOT NULL ,
  `user_id` BIGINT NOT NULL ,
  `text_id` BIGINT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_dict_lang1` (`lang_id` ASC) ,
  INDEX `fk_dict_user1` (`user_id` ASC) ,
  INDEX `fk_dict_text1` (`text_id` ASC) ,
  CONSTRAINT `fk_dict_lang1`
    FOREIGN KEY (`lang_id` )
    REFERENCES `lang` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_dict_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `user` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_dict_text1`
    FOREIGN KEY (`text_id` )
    REFERENCES `text` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job` ;

CREATE  TABLE IF NOT EXISTS `job` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `progress` INT NOT NULL DEFAULT 0 ,
  `details` VARCHAR(255) NULL DEFAULT NULL ,
  `active` TINYINT(1)  NOT NULL DEFAULT FALSE ,
  `transition_id` BIGINT NOT NULL ,
  `dict_id` BIGINT NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_job_transition1` (`transition_id` ASC) ,
  INDEX `fk_job_dict1` (`dict_id` ASC) ,
  CONSTRAINT `fk_job_transition1`
    FOREIGN KEY (`transition_id` )
    REFERENCES `transition` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_job_dict1`
    FOREIGN KEY (`dict_id` )
    REFERENCES `dict` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `translator_provider`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `translator_provider` ;

CREATE  TABLE IF NOT EXISTS `translator_provider` (
  `id` BIGINT NOT NULL ,
  `title` VARCHAR(32) NULL DEFAULT NULL ,
  `host` VARCHAR(64) NULL DEFAULT NULL ,
  `req_pattern` VARCHAR(255) NULL DEFAULT NULL ,
  `res_pattern` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `word` ;

CREATE  TABLE IF NOT EXISTS `word` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `lang_id` VARCHAR(2) NOT NULL ,
  `value` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `words_Unique` (`value` ASC, `lang_id` ASC) )
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `translator`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `translator` ;

CREATE  TABLE IF NOT EXISTS `translator` (
  `id` BIGINT NOT NULL ,
  `service_id` BIGINT NOT NULL ,
  `src_lang_id` VARCHAR(2) NOT NULL ,
  `dest_lang_id` VARCHAR(2) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_translator_service1` (`service_id` ASC) ,
  INDEX `fk_translator_src_lang1` (`src_lang_id` ASC) ,
  INDEX `fk_translator_dest_lang2` (`dest_lang_id` ASC) ,
  CONSTRAINT `fk_translator_service1`
    FOREIGN KEY (`service_id` )
    REFERENCES `translator_provider` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_translator_src_lang1`
    FOREIGN KEY (`src_lang_id` )
    REFERENCES `lang` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_translator_dest_lang2`
    FOREIGN KEY (`dest_lang_id` )
    REFERENCES `lang` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `translation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `translation` ;

CREATE  TABLE IF NOT EXISTS `translation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `value` VARCHAR(64) NULL DEFAULT NULL ,
  `word_id` BIGINT NOT NULL ,
  `translator_id` BIGINT,
  `user_id` BIGINT,
  `pre_text` VARCHAR(128) NULL DEFAULT NULL ,
  `post_text` VARCHAR(128) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_translation_word1` (`word_id` ASC) ,
  INDEX `fk_translation_translator1` (`translator_id` ASC) ,
  UNIQUE INDEX `word_id_UNIQUE` (`word_id` ASC, `translator_id` ASC) ,
  CONSTRAINT `fk_translation_word1`
    FOREIGN KEY (`word_id` )
    REFERENCES `word` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_translation_translator1`
    FOREIGN KEY (`translator_id` )
    REFERENCES `translator` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dict_word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dict_word` ;

CREATE  TABLE IF NOT EXISTS `dict_word` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `counter` BIGINT NOT NULL ,
  `dict_id` BIGINT NOT NULL ,
  `word_id` BIGINT NOT NULL ,
  `translation` VARCHAR(64) NULL ,
  `translator` VARCHAR(32) NULL ,
  `occurances` TEXT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_dict_word_dict1` (`dict_id` ASC) ,
  INDEX `fk_dict_word_word1` (`word_id` ASC) ,
  CONSTRAINT `fk_dict_word_dict1`
    FOREIGN KEY (`dict_id` )
    REFERENCES `dict` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_dict_word_word1`
    FOREIGN KEY (`word_id` )
    REFERENCES `word` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
