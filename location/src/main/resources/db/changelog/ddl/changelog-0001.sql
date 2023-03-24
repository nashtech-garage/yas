--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-474-1
create table country (id bigserial not null, name varchar(255) not null);
create table state_or_province (id bigserial not null, name varchar(255) not null);
create table district (id bigserial not null, name varchar(255) not null);
create table address (id bigserial not null, streetName varchar(255) not null, no varchar(255) not null);

