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