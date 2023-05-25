--liquibase formatted sql

--changeset lecamhoanglam:issue-440-2
create table payment (
   id bigserial not null,
   order_id bigserial not null,
   payment_fee decimal(19,2),
   amount decimal(19,2),
   payment_method varchar(255),
   payment_status varchar(255),
   created_on timestamp(6),
   last_modified_on timestamp(6),
   PRIMARY KEY (id)
);
