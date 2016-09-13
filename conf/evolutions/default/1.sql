# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table item (
  id                        integer not null,
  name                      varchar(255),
  description               varchar(255),
  quantity                  integer,
  num_sold                  integer,
  price                     varchar(255),
  sale_price                double,
  lower_limit               double,
  url                       varchar(255),
  constraint pk_item primary key (id))
;

create table transaction (
  tran_id                   varchar(255) not null,
  buyer_id                  varchar(255),
  buyer                     varchar(255),
  total                     double,
  cash                      double,
  card                      double,
  change                    double,
  constraint pk_transaction primary key (tran_id))
;

create table user (
  id                        integer not null,
  username                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  vpassword                 varchar(255),
  logged_in                 boolean,
  total                     double,
  login_count               integer,
  status                    integer,
  constraint ck_user_status check (status in (0,1)),
  constraint pk_user primary key (id))
;

create sequence item_seq;

create sequence transaction_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists item;

drop table if exists transaction;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists item_seq;

drop sequence if exists transaction_seq;

drop sequence if exists user_seq;

