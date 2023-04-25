--liquibase formatted sql

--changeset lecamhoanglam:shipment
create table payment (
   id bigserial not null,
   order_id bigserial not null,
   warehouse_id bigserial not null,
   tracking_number varchar(255),
   created_by varchar(255),
   created_on timestamp(6),
   last_modified_by varchar(255),
   last_modified_on timestamp(6),
   PRIMARY KEY (id)
);