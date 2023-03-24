--liquibase formatted sql

--changeset dieunguyen:issue-001
alter table if exists country add primary key (id);
alter table if exists state_or_province alter column name type varchar(450);
alter table if exists state_or_province add column code varchar(450);
alter table if exists state_or_province add column type varchar(450);
alter table if exists state_or_province add column country_id bigserial not null;
alter table if exists state_or_province add constraint FK2y94svpmqttx80mshyny85wqr foreign key (country_id) references country;