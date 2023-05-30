
create table payment_provider (
   id                           varchar(255) not null,
   is_enabled                   boolean,
   name                         varchar(255),
   configure_url                varchar(255),
   landing_view_component_name  varchar(255),
   additional_settings          text,
   PRIMARY KEY (id)
);