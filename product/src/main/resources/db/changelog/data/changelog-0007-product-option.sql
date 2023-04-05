--liquibase formatted sql

--changeset vonhu:product-option1
insert into product_option (name) values ('COLOR')

--changeset vonhu:product-option2
insert into product_option (name) values ('CPU')

--changeset vonhu:product-option3
insert into product_option (name) values ('RAM')

--changeset vonhu:product-option4
insert into product_option (name) values ('OS')

--changeset khoahd7621:issue-586
insert into product_option (name) values ('Storage')