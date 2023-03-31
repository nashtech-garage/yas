--liquibase formatted sql

--changeset dieunguyen:issue-tax-001
create table tax_class (id bigserial not null, name varchar(255) not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
create table tax_rate (id bigserial not null, rate float(53) not null, zip_code varchar(25), tax_class_id bigserial not null, state_or_province_id bigserial not null, country_id bigserial not null , created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
alter table if exists tax_rate add constraint FKkud35ls1d40wpjb5htpp14yua foreign key (tax_class_id) references tax_class;
