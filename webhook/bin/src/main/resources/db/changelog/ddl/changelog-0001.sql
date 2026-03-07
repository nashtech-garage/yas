--liquibase formatted sql

--changeset minhtran:issue-002
create table webhook (id bigserial not null, payload_url text not null, content_type varchar(128), secret varchar(512), is_active bool, created_by varchar(256), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
create table event (id bigserial not null, name varchar(512) not null, description text, primary key (id));
create table webhook_event (id bigserial not null, webhook_id bigserial references webhook(id), event_id bigserial references event(id), primary key (id));