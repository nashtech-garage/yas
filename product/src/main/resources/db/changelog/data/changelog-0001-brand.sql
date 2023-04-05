--liquibase formatted sql

--changeset khanhtran:issue-335-4
insert into brand (name, slug) values ('Apple', 'apple');

--changeset khanhtran:issue-335-5
insert into brand (name, slug) values ('Samsung', 'samsung');

--changeset khoahd7621:issue-586
insert into brand (name, slug) values ('Dell', 'dell');
