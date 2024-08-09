--liquibase formatted sql

--changeset dieunguyen:issue-800
INSERT INTO tax_class ("name", created_by, created_on, last_modified_by, last_modified_on) VALUES('Value Added Tax (VAT)', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:14:58.952', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:40.758');
INSERT INTO tax_rate (rate, zip_code, created_by, created_on, last_modified_by, last_modified_on) VALUES(8.0, '70000', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:22.909', '6a4ccf58-14a7-4c68-8f35-9107f98755b2', '2024-08-01 04:16:22.909');
