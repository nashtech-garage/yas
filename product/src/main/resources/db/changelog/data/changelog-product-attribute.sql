--liquibase formatted sql

--changeset vonhu:product_attribute1
insert into product_attribute (name,product_attribute_group_id) values ('CPU', 1)

--changeset vonhu:product_attribute2
insert into product_attribute (name,product_attribute_group_id) values ('GPU', 1)

--changeset vonhu:product_attribute3
insert into product_attribute (name,product_attribute_group_id) values ('OS', 1)

--changeset vonhu:product_attribute4
insert into product_attribute (name,product_attribute_group_id) values ('Size', 2)

--changeset vonhu:product_attribute5
insert into product_attribute (name,product_attribute_group_id) values ('Type', 2)

--changeset vonhu:product_attribute6
insert into product_attribute (name,product_attribute_group_id) values ('Bluetooth', 3)

--changeset vonhu:product_attribute7
insert into product_attribute (name,product_attribute_group_id) values ('NFC', 3)

--changeset vonhu:product_attribute8
insert into product_attribute (name,product_attribute_group_id) values ('Main Camera', 4)

--changeset vonhu:product_attribute9
insert into product_attribute (name,product_attribute_group_id) values ('Sub Camera', 4)