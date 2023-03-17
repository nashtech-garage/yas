--liquibase formatted sql

--changeset lecamhoanglam:issue-440-1
create table orders (id bigserial not null);
