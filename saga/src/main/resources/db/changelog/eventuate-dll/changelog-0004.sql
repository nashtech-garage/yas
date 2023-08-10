--liquibase formatted sql
--changeset bangnguyen:eventuate-dll-0004
--comment: init eventuate tables

CREATE SEQUENCE eventuate.message_table_id_sequence START 1;

select setval('eventuate.message_table_id_sequence', (ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000))::BIGINT);

CREATE TABLE eventuate.new_message
(
    dbid              BIGINT NOT NULL DEFAULT nextval('eventuate.message_table_id_sequence') PRIMARY KEY,
    id                VARCHAR(1000),
    destination       TEXT   NOT NULL,
    headers           TEXT   NOT NULL,
    payload           TEXT   NOT NULL,
    published         SMALLINT        DEFAULT 0,
    message_partition SMALLINT,
    creation_time     BIGINT
);

ALTER SEQUENCE eventuate.message_table_id_sequence OWNED BY eventuate.new_message.dbid;

INSERT INTO eventuate.new_message (id, destination, headers, payload, published, message_partition, creation_time)
SELECT id, destination, headers, payload, published, message_partition, creation_time
FROM eventuate.message
ORDER BY id;

DROP TABLE eventuate.message;

ALTER TABLE eventuate.new_message
    RENAME TO message;

CREATE SEQUENCE eventuate.events_table_id_sequence START 1;

select setval('eventuate.events_table_id_sequence', (ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000))::BIGINT);

CREATE TABLE eventuate.new_events
(
    id               BIGINT        NOT NULL DEFAULT nextval('eventuate.events_table_id_sequence') PRIMARY KEY,
    event_id         VARCHAR(1000),
    event_type       VARCHAR(1000),
    event_data       VARCHAR(1000) NOT NULL,
    entity_type      VARCHAR(1000) NOT NULL,
    entity_id        VARCHAR(1000) NOT NULL,
    triggering_event VARCHAR(1000),
    metadata         VARCHAR(1000),
    published        SMALLINT               DEFAULT 0
);

ALTER SEQUENCE eventuate.events_table_id_sequence OWNED BY eventuate.new_events.id;

INSERT INTO eventuate.new_events (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata,
                                  published)
SELECT event_id,
       event_type,
       event_data,
       entity_type,
       entity_id,
       triggering_event,
       metadata,
       published
FROM eventuate.events
ORDER BY event_id;

DROP TABLE eventuate.events;

ALTER TABLE eventuate.new_events
    RENAME TO events;

CREATE INDEX events_idx ON eventuate.events (entity_type, entity_id, id);
CREATE INDEX events_published_idx ON eventuate.events (published, id);