--liquibase formatted sql

--changeset vonhu:issue-promotion-001
create table promotion(id bigserial not null, name varchar(255) not null, description varchar(255) null,
coupon_code varchar(255), start_date timestamp(6), end_date timestamp(6), value bigserial, amount bigserial,
created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
