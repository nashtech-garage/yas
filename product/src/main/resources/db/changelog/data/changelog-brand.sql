--liquibase formatted sql

--changeset khanhtran:issue-335-2
insert into brand (name, slug) values ('Apple', 'apple');

--changeset khanhtran:issue-335-3
insert into brand (name, slug) values ('Samsung', 'samsung');
