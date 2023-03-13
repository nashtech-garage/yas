--liquibase formatted sql

--changeset khanhtran:issue-335-1
create table brand (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), name varchar(255), slug varchar(255), primary key (id));
create table category (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), description varchar(255), display_order smallint, meta_description varchar(255), meta_keyword varchar(255), name varchar(255), slug varchar(255), parent_id bigint, primary key (id));
create table product (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), description varchar(255), gtin varchar(255), has_options boolean, is_allowed_to_order boolean, is_featured boolean, is_published boolean, is_visible_individually boolean, meta_description varchar(255), meta_keyword varchar(255), meta_title varchar(255), name varchar(255), price float(53), short_description varchar(255), sku varchar(255), slug varchar(255), specification varchar(255), thumbnail_media_id bigint, brand_id bigint, parent_id bigint,  is_active boolean,primary key (id));
create table product_attribute (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), name varchar(255), product_attribute_group_id bigint, primary key (id));
create table product_attribute_group (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), name varchar(255), primary key (id));
create table product_attribute_value (id bigserial not null, value varchar(255), product_id bigint not null, product_attribute_id bigint not null, primary key (id));
create table product_category (id bigserial not null, display_order integer not null, is_featured_product boolean not null, category_id bigint not null, product_id bigint not null, primary key (id));
create table product_image (id bigserial not null, image_id bigint, product_id bigint not null, primary key (id));
create table product_option (id bigserial not null, created_by varchar(255), created_on timestamp(6), last_modified_by varchar(255), last_modified_on timestamp(6), name varchar(255), primary key (id));
create table product_option_combination (id bigserial not null, display_order integer not null, value varchar(255), product_id bigint not null, product_option_id bigint not null, primary key (id));
create table product_option_value (id bigserial not null, display_order integer not null, display_type varchar(255), value varchar(255), product_id bigint not null, product_option_id bigint not null, primary key (id));
alter table if exists category add constraint FK2y94svpmqttx80mshyny85wqr foreign key (parent_id) references category;
alter table if exists product add constraint FKs6cydsualtsrprvlf2bb3lcam foreign key (brand_id) references brand;
alter table if exists product add constraint FKgmb19wbjvpu06559t7w33wqoc foreign key (parent_id) references product;
alter table if exists product_attribute add constraint FKrj91gkq3vj79a1l1tmrncwaiv foreign key (product_attribute_group_id) references product_attribute_group;
alter table if exists product_attribute_value add constraint FKejch53yke5ufe6w72yjpnakoi foreign key (product_id) references product;
alter table if exists product_attribute_value add constraint FKqgk2xbdl46wt0h9i5uheps5ke foreign key (product_attribute_id) references product_attribute;
alter table if exists product_category add constraint FKkud35ls1d40wpjb5htpp14q4e foreign key (category_id) references category;
alter table if exists product_category add constraint FK2k3smhbruedlcrvu6clued06x foreign key (product_id) references product;
alter table if exists product_image add constraint FK6oo0cvcdtb6qmwsga468uuukk foreign key (product_id) references product;
alter table if exists product_option_combination add constraint FKa4qh7bl0gs7m2oyiqcbcy8yx4 foreign key (product_id) references product;
alter table if exists product_option_combination add constraint FK3r4w3siw3js19wv4lsd7cfuj1 foreign key (product_option_id) references product_option;
alter table if exists product_option_value add constraint FK2orsp8e9oenavxnq98nbxrm1s foreign key (product_id) references product;
alter table if exists product_option_value add constraint FKhsycxdkv3mfsvu2l8f1i1vy11 foreign key (product_option_id) references product_option;