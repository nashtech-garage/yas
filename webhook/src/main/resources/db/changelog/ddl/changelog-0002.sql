--liquibase formatted sql

--changeset minhtran:issue-003
create table webhook_event_notification (id bigserial not null, webhook_event_id bigserial references webhook_event(id), payload jsonb, notification_status varchar(128), created_on timestamp, primary key (id));