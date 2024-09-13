--liquibase formatted sql

--changeset hoanglam:update paypal and cod id
UPDATE payment_provider
SET id = 'PAYPAL'
WHERE id = 'PaypalPayment';


UPDATE payment_provider
SET id = 'COD'
WHERE id = 'CodPayment';
