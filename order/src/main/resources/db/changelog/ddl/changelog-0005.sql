create table order_address (
   id bigserial not null,
   contact_name varchar(255) not null,
   phone varchar(255) not null,
   address_line1 varchar(255) not null,
   address_line2 varchar(255),
   city varchar(255),
   zip_code varchar(255),
   district bigserial,
   state_or_province bigserial,
   country bigserial,
   district_name varchar(255),
   state_or_province_name varchar(255),
   country_name varchar(255),
   PRIMARY KEY (id)
);

alter table if exists "order"
ADD CONSTRAINT FK_BillingAddress FOREIGN KEY (billing_address_id) REFERENCES order_address (id),
ADD CONSTRAINT FKk_ShippingAddress FOREIGN KEY (shipping_address_id) REFERENCES order_address (id),
drop column phone;

alter table if exists order_item
add column discount_amount decimal(19,2);