
alter table if exists "order"
add column phone varchar(255),
add column address varchar(255),
add column note varchar(255),
add column tax float(53),
add column discount float(53),
add column number_item integer not null,
add column total_price decimal(19,2),
add column delivery_fee decimal(19,2),
add column delivery_method varchar(255),
add column delivery_status varchar(255),
add column payment_method varchar(255),
add primary key (id);

create table order_item (
   id bigserial not null,
   product_id bigserial not null,
   quantity integer,
   price decimal(19,2),
   note varchar(255),
   order_id bigserial not null, 
   PRIMARY KEY (id),
   CONSTRAINT FK_OrderOrderItem FOREIGN KEY (order_id) REFERENCES "order" (id)
);



