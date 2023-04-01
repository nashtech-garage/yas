--liquibase formatted sql

--changeset vonhu:product_attribute_group1
insert into product_attribute_group (name) values ('General')

--changeset vonhu:product_attribute_group2
insert into product_attribute_group (name) values ('Screen')

--changeset vonhu:product_attribute_group3
insert into product_attribute_group (name) values ('Connectivity')

--changeset vonhu:product_attribute_group4
insert into product_attribute_group (name) values ('Camera')