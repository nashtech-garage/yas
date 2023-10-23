--liquibase formatted sql

--changeset khanhdt:product_template1
insert into product_template (name) values
    ('Tablet');

--changeset khanhdt:product_template2
insert into product_template (name) values
    ('Iphone')

--changeset khanhdt:product_template_attribute
insert into product_attribute_template (product_attribute_id, product_template_id, display_order) values
    (3, 1, 0),
    (6, 2, 0);