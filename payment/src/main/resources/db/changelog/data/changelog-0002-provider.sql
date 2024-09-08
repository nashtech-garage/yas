--liquibase formatted sql

--changeset hieunc:cod
insert into payment_provider (id, is_enabled, name, configure_url, landing_view_component_name, additional_settings)
    values ('COD', 'true', 'Cash on Delivery', 'cod-config', 'cod landing view', '');


