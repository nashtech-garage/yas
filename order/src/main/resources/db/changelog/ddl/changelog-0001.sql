--liquibase formatted sql

--changeset lecamhoanglam:issue-440-2
create table "order" (id bigserial not null);
