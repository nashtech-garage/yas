--liquibase formatted sql

--changeset khanhtran:issue-335-2
insert into brand (name, slug) values ('Apple', 'Apple');

--changeset khanhtran:issue-335-3
insert into brand (name, slug) values ('Samsung', 'Samsung');