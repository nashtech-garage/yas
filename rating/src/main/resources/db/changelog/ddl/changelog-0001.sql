--liquibase formatted sql

--changeset khanhtran:issue-335-1
create table rating (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), content varchar(255), first_name varchar(255), last_name varchar(255), product_id bigint, rating_star integer not null, primary key (id));
