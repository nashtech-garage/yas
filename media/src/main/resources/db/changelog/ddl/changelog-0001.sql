--liquibase formatted sql

--changeset bangvan:issue-848
CREATE TABLE media
(
    id         bigserial    NOT NULL,
    caption    varchar(255) NULL,
    file_name  varchar(255) NULL,
    file_path  text         NULL,
    media_type varchar(128) NULL,
    CONSTRAINT media_pkey PRIMARY KEY (id)
);