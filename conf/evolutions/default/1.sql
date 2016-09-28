# --- !Ups

CREATE TABLE User_table (
  id                 BIGSERIAL NOT NULL,
  email              VARCHAR(255),
  fullname           VARCHAR(255),
  confirmation_token VARCHAR(255),
  password_hash      VARCHAR(255),
  date_creation      TIMESTAMP,
  validated          BOOLEAN,
  CONSTRAINT uq_User_table_email UNIQUE (email),
  CONSTRAINT uq_User_table_fullname UNIQUE (fullname),
  CONSTRAINT pk_User_table PRIMARY KEY (id)
);

CREATE TABLE vk_activity_history (
  id           BIGSERIAL NOT NULL,
  history_date TIMESTAMP,
  CONSTRAINT pk_vk_activity_history PRIMARY KEY (id)
);

CREATE TABLE vk_enter_exit_history (
  id           BIGSERIAL NOT NULL,
  history_date TIMESTAMP,
  vkGroupId    BIGINT    NOT NULL,
  vkUserId     BIGINT    NOT NULL,
  status       INTEGER,
  CONSTRAINT ck_vk_enter_exit_history_status CHECK (status IN (0, 1, 2)),
  CONSTRAINT pk_vk_enter_exit_history PRIMARY KEY (id)
);

CREATE TABLE vk_group (
  id           BIGSERIAL       NOT NULL,
  vk_id        VARCHAR(255),
  name         VARCHAR(255),
  is_closed    INTEGER,
  type         VARCHAR(255),
  photo        VARCHAR(255),
  photo_medium VARCHAR(255),
  photo_big    VARCHAR(255),
  update_count INT DEFAULT '0' NOT NULL,
  CONSTRAINT pk_vk_group PRIMARY KEY (id)
);

CREATE TABLE vk_user (
  id         BIGSERIAL NOT NULL,
  vk_id      BIGINT,
  first_name VARCHAR(255),
  last_name  VARCHAR(255),
  CONSTRAINT uq_vk_user_vk_id UNIQUE (vk_id),
  CONSTRAINT pk_vk_user PRIMARY KEY (id)
);


CREATE TABLE user_vkGroup (
  userId    BIGINT NOT NULL,
  vkGroupId BIGINT NOT NULL,
  CONSTRAINT pk_user_vkGroup PRIMARY KEY (userId, vkGroupId)
);

CREATE TABLE vkgroup_vkuser (
  vk_group_id BIGINT NOT NULL,
  vk_user_id  BIGINT NOT NULL,
  CONSTRAINT pk_vkgroup_vkuser PRIMARY KEY (vk_group_id, vk_user_id)
);
ALTER TABLE vk_activity_history
  ADD CONSTRAINT fk_vk_activity_history_vkUser_1 FOREIGN KEY (id) REFERENCES vk_user (id);
ALTER TABLE vk_enter_exit_history
  ADD CONSTRAINT fk_vk_enter_exit_history_vkGro_2 FOREIGN KEY (vkGroupId) REFERENCES vk_group (id);
ALTER TABLE vk_enter_exit_history
  ADD CONSTRAINT fk_vk_enter_exit_history_vkUse_3 FOREIGN KEY (vkUserId) REFERENCES vk_user (id);

ALTER TABLE user_vkGroup
  ADD CONSTRAINT fk_user_vkGroup_User_table_01 FOREIGN KEY (userId) REFERENCES User_table (id);

ALTER TABLE user_vkGroup
  ADD CONSTRAINT fk_user_vkGroup_vk_group_02 FOREIGN KEY (vkGroupId) REFERENCES vk_group (id);

ALTER TABLE vkgroup_vkuser
  ADD CONSTRAINT fk_vkgroup_vkuser_vk_group_01 FOREIGN KEY (vk_group_id) REFERENCES vk_group (id);

ALTER TABLE vkgroup_vkuser
  ADD CONSTRAINT fk_vkgroup_vkuser_vk_user_02 FOREIGN KEY (vk_user_id) REFERENCES vk_user (id);

# --- !Downs

DROP TABLE IF EXISTS User_table CASCADE;

DROP TABLE IF EXISTS user_vkGroup CASCADE;

DROP TABLE IF EXISTS vk_activity_history CASCADE;

DROP TABLE IF EXISTS vk_enter_exit_history CASCADE;

DROP TABLE IF EXISTS vk_group CASCADE;

DROP TABLE IF EXISTS vkgroup_vkuser CASCADE;

DROP TABLE IF EXISTS vk_user CASCADE;

