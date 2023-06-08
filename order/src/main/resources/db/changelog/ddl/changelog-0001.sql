--liquibase formatted sql

--changeset lecamhoanglam:issue-440-2
create table "order" (
    id bigserial            not null,
    email                   varchar(255),
    note                    varchar(255),
    tax                     float(53),
    discount                float(53),
    number_item             integer,
    total_price             decimal(19,2),
    delivery_fee            decimal(19,2),
    delivery_method         varchar(255),
    delivery_status         varchar(255),
    payment_method          varchar(255),
    payment_status          varchar(255),
    payment_id              bigint,
    created_by              varchar(255),
    created_on              timestamp(6),
    last_modified_by        varchar(255),
    last_modified_on        timestamp(6),
    order_status            varchar(255),
    coupon_code             varchar(255),
    shipping_address_id     bigserial not null,
    billing_address_id      bigserial not null,
    CONSTRAINT FK_BillingAddress FOREIGN KEY (billing_address_id) REFERENCES order_address (id),
    CONSTRAINT FKk_ShippingAddress FOREIGN KEY (shipping_address_id) REFERENCES order_address (id),
    PRIMARY KEY (id)
);


create table order_item (
   id bigserial not null,
   product_id bigserial not null,
   product_name varchar(255),
   product_price varchar(255),
   quantity integer,
   price decimal(19,2),
   note varchar(255),
   tax_amount decimal(19,2),
   discount_amount decimal(19,2),
   tax_percent decimal(19,2),
   order_id bigserial not null,
   PRIMARY KEY (id),
   CONSTRAINT FK_OrderOrderItem FOREIGN KEY (order_id) REFERENCES "order" (id)
);

create table order_address (
   id bigserial not null,
   contact_name varchar(255) not null,
   phone varchar(255) not null,
   address_line1 varchar(255) not null,
   address_line2 varchar(255),
   city varchar(255),
   zip_code varchar(255),
   district_id bigserial,
   state_or_province_id bigserial,
   country_id bigserial,
   district_name varchar(255),
   state_or_province_name varchar(255),
   country_name varchar(255),
   PRIMARY KEY (id)
);

create table checkout (
   id varchar(255) not null,
   email varchar(255),
   note varchar(255),
   coupon_code varchar(255),
   checkout_state  varchar(255),
   created_by varchar(255),
   created_on timestamp(6),
   last_modified_by varchar(255),
   last_modified_on timestamp(6),
   PRIMARY KEY (id)
);

create table checkout_item (
   id bigserial not null,
   product_id bigserial,
   product_name varchar(255),
   quantity integer,
   product_price decimal(19,2),
   note varchar(255),
   discount_amount decimal(19,2),
   tax_amount decimal(19,2),
   tax_percent decimal(19,2),
   checkout_id varchar(255) not null,
   PRIMARY KEY (id),
   CONSTRAINT FK_CheckoutItem FOREIGN KEY (checkout_id) REFERENCES checkout (id)
);

