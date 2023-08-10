--liquibase formatted sql
--changeset bangnguyen:eventuate-dll-0002
--comment: init eventuate tables

DROP TABLE IF EXISTS eventuate.message CASCADE;
DROP TABLE IF EXISTS eventuate.received_messages CASCADE;

CREATE TABLE eventuate.message
(
    id                VARCHAR(1000) PRIMARY KEY,
    destination       TEXT NOT NULL,
    headers           TEXT NOT NULL,
    payload           TEXT NOT NULL,
    published         SMALLINT DEFAULT 0,
    message_partition SMALLINT,
    creation_time     BIGINT
);

CREATE INDEX message_published_idx ON eventuate.message (published, id);

CREATE TABLE eventuate.received_messages
(
    consumer_id   VARCHAR(1000),
    message_id    VARCHAR(1000),
    creation_time BIGINT,
    published     SMALLINT DEFAULT 0,
    PRIMARY KEY (consumer_id, message_id)
);

CREATE TABLE eventuate.offset_store
(
    client_name       VARCHAR(255) NOT NULL PRIMARY KEY,
    serialized_offset VARCHAR(255)
);