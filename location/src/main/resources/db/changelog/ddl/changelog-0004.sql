--liquibase formatted sql

--changeset dieunguyen:issue-001
alter table if exists district add primary key (id);
alter table if exists state_or_province add primary key (id);
alter table if exists district alter column name type varchar(450);
alter table if exists district add column type varchar(450);
alter table if exists district add column location varchar(450);
alter table if exists district add column state_or_province_id bigserial not null;
alter table if exists district add constraint FK2y94svpmqttx80mshyny85wqr foreign key (state_or_province_id) references state_or_province;