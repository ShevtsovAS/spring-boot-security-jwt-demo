create database security_demo;
drop table if exists app_users cascade;
drop table if exists roles cascade;
drop table if exists user_roles cascade;
drop sequence if exists global_seq;

CREATE SEQUENCE global_seq START 100000;

/*==============================================================*/
/* Table: app_users                                             */
/*==============================================================*/
create table app_users
(
    id       INTEGER PRIMARY KEY         DEFAULT nextval('global_seq'),
    username VARCHAR(60) UNIQUE NOT NULL,
    password VARCHAR(60)        NOT NULL,
    active   BOOLEAN            NOT NULL DEFAULT TRUE
);

comment on column app_users.id is
    'Идентификатор пользователя';

comment on column app_users.username is
    'Имя пользователя';

comment on column app_users.password is
    'Пароль пользователя в зашифрованном виде';

comment on column app_users.active is
    'Признак пользователь активирован';

/*==============================================================*/
/* Table: roles                                                 */
/*==============================================================*/
create table roles
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    role_name VARCHAR(255) NOT NULL UNIQUE
);

comment on column roles.id is
    'Идентификатор роли';

comment on column roles.role_name is
    'Имя роли';

/*==============================================================*/
/* Table: user_roles                                            */
/*==============================================================*/

create table user_roles
(
    user_id INTEGER REFERENCES app_users (id),
    role_id INTEGER REFERENCES roles (id),
    primary key (user_id, role_id)
);

comment on column user_roles.user_id is
    'Идентификатор пользователя';

comment on column user_roles.role_id is
    'Идентификатор роли';