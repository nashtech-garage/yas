--liquibase formatted sql

--changeset minhtran:issue-001
INSERT INTO public.event(name, description) VALUES ('ON_PRODUCT_UPDATED','The product is updated.');
INSERT INTO public.event(name, description) VALUES ('ON_ORDER_CREATED','The order is created.');
INSERT INTO public.event(name, description) VALUES ('ON_ORDER_STATUS_UPDATED','The order status is updated.');