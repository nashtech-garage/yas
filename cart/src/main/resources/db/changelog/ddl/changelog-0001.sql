--liquibase formatted sql

--changeset khanhtran:issue-335-1
create sequence cart_item_seq start with 1 increment by 50;
create sequence cart_seq start with 1 increment by 50;
create table cart (id bigint not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), customer_id varchar(255), primary key (id));
create table cart_item (id bigint not null, quantity integer not null, parent_product_id bigint, product_id bigint, cart_id bigint not null, primary key (id));
alter table if exists cart_item add constraint FK1uobyhgl1wvgt1jpccia8xxs3 foreign key (cart_id) references cart;