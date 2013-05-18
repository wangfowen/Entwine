# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table common_time_block (
  id                        bigint not null,
  start_time                timestamp not null,
  end_time                  timestamp not null,
  event_id                  bigint,
  constraint pk_common_time_block primary key (id))
;

create table email_account (
  id                        bigint not null,
  email                     varchar(255) not null,
  constraint pk_email_account primary key (id))
;

create table event (
  id                        bigint not null,
  name                      varchar(255) not null,
  description               varchar(255),
  location                  varchar(255),
  status                    integer not null,
  owner_id                  bigint,
  start_date                timestamp,
  end_date                  timestamp,
  cutoff_date               timestamp,
  constraint ck_event_status check (status in ('1','30','40','10','20')),
  constraint pk_event primary key (id))
;

create table full_account (
  id                        bigint not null,
  fullname                  varchar(255),
  alias                     varchar(255),
  password                  varchar(255),
  email_account_id          bigint,
  constraint pk_full_account primary key (id))
;

create table participation (
  id                        bigint not null,
  status                    integer,
  role                      integer,
  participant_id            bigint,
  event_id                  bigint,
  constraint pk_participation primary key (id))
;

create table subscriber (
  id                        bigint not null,
  email_account_id          bigint,
  constraint pk_subscriber primary key (id))
;

create table time_block (
  id                        bigint not null,
  participation_id          bigint,
  start_time                timestamp not null,
  end_time                  timestamp not null,
  constraint pk_time_block primary key (id))
;


create table ctb_account (
  ctb_id                         bigint not null,
  account_id                     bigint not null,
  constraint pk_ctb_account primary key (ctb_id, account_id))
;
create sequence common_time_block_seq;

create sequence email_account_seq;

create sequence event_seq;

create sequence full_account_seq;

create sequence participation_seq;

create sequence subscriber_seq;

create sequence time_block_seq;

alter table common_time_block add constraint fk_common_time_block_event_1 foreign key (event_id) references event (id);
create index ix_common_time_block_event_1 on common_time_block (event_id);
alter table event add constraint fk_event_owner_2 foreign key (owner_id) references full_account (id);
create index ix_event_owner_2 on event (owner_id);
alter table full_account add constraint fk_full_account_emailAccount_3 foreign key (email_account_id) references email_account (id);
create index ix_full_account_emailAccount_3 on full_account (email_account_id);
alter table participation add constraint fk_participation_participant_4 foreign key (participant_id) references email_account (id);
create index ix_participation_participant_4 on participation (participant_id);
alter table participation add constraint fk_participation_event_5 foreign key (event_id) references event (id);
create index ix_participation_event_5 on participation (event_id);
alter table subscriber add constraint fk_subscriber_emailAccount_6 foreign key (email_account_id) references email_account (id);
create index ix_subscriber_emailAccount_6 on subscriber (email_account_id);
alter table time_block add constraint fk_time_block_participation_7 foreign key (participation_id) references participation (id);
create index ix_time_block_participation_7 on time_block (participation_id);



alter table ctb_account add constraint fk_ctb_account_common_time_bl_01 foreign key (ctb_id) references common_time_block (id);

alter table ctb_account add constraint fk_ctb_account_email_account_02 foreign key (account_id) references email_account (id);

# --- !Downs

drop table if exists common_time_block cascade;

drop table if exists ctb_account cascade;

drop table if exists email_account cascade;

drop table if exists event cascade;

drop table if exists full_account cascade;

drop table if exists participation cascade;

drop table if exists subscriber cascade;

drop table if exists time_block cascade;

drop sequence if exists common_time_block_seq;

drop sequence if exists email_account_seq;

drop sequence if exists event_seq;

drop sequence if exists full_account_seq;

drop sequence if exists participation_seq;

drop sequence if exists subscriber_seq;

drop sequence if exists time_block_seq;

