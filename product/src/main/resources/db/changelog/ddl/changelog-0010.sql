create table product_template (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6),  name varchar(255), primary key (id));
create table product_attribute_template (id bigserial not null, product_attribute_id bigint not null, product_template_id bigint not null, display_order integer not null,primary key (id));
alter table if exists product_attribute_template add constraint FK_template_atributetemplate foreign key (product_template_id) references product_template;
alter table if exists product_attribute_template add constraint FK_attribute_atributetemplate foreign key (product_attribute_id) references product_attribute;
