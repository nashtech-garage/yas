--liquibase formatted sql

--changeset lecamhoanglam:issue-440-1
create table order (id bigserial not null);
