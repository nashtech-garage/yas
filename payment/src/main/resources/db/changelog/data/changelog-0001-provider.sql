--liquibase formatted sql

--changeset hoanglam:paypal
insert into payment_provider (id, is_enabled, name, configure_url, landing_view_component_name, additional_settings)
    values ('PaypalPayment', 'true', 'paypal', 'paypal-config', 'paypal landing view', '{"clientId": "AW7GUe26RhVRlWKHeKHjl43ZqON8NFgJbEOljFDkuBiLlFYWj7mskz77QgVMHkl2M9VBMA5jWMFwxRll", "clientSecret": "ENX1js2V-5bdTe86voCAcyrT6bcRtVzRcvNsdo0XDhDYT_5KEBmlYT7oOBWVVwjPsS4i6bktL1R8eL3j", "mode": "sandbox"}');


