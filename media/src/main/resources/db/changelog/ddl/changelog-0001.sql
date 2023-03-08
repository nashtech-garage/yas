--liquibase formatted sql

--changeset khanhtran:issue-335-1
create table media (id bigserial not null, caption varchar(255), data oid, file_name varchar(255), media_type varchar(255), primary key (id));
