--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-483-1
create table inventory (id bigserial not null, product_id bigint not null, quantity integer not null);

--changeset dieunguyen:issue-483-2
drop table inventory;
create table warehouse (id bigserial not null, name varchar(450) not null, addressId bigserial, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
create table stock (id bigserial not null, warehouse_id bigserial not null, productId bigserial not null, quantity bigserial, reserved_quantity bigserial, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));
create table stock_history (id bigserial not null, warehouse_id bigserial not null, productId bigserial not null, adjusted_quantity bigserial, note varchar(450), created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), primary key (id));

alter table if exists stock add constraint FKkud35ls1d40wpjb5htpp14yuas foreign key (warehouse_id) references warehouse;
alter table if exists stock_history add constraint FKkud35ls1d40wpjb5htpp14yuh foreign key (warehouse_id) references warehouse;

--changeset nguyenvanhadncntt:issue-629-1
ALTER TABLE warehouse RENAME COLUMN addressId TO address_id;
ALTER TABLE stock RENAME COLUMN productId TO product_id;
ALTER TABLE stock_history RENAME COLUMN productId TO product_id;