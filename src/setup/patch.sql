ALTER TABLE account ADD COLUMN 'native_lang_id' VARCHAR(2) AFTER 'enabled';

ALTER TABLE translation ADD COLUMN 'account_id' BIGINT AFTER 'translator_id';
ALTER TABLE translation ADD COLUMN 'pre_text' VARCHAR(128) NULL DEFAULT NULL;
ALTER TABLE translation ADD COLUMN 'post_text' VARCHAR(128) NULL DEFAULT NULL;

CREATE  TABLE IF NOT EXISTS 'account_lang' (
  'id' BIGINT NOT NULL AUTO_INCREMENT ,
  'account_id' BIGINT NOT NULL ,
  'lang_id' VARCHAR(2) NOT NULL,
  PRIMARY KEY ('id'));

