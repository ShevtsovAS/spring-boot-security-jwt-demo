create database security_demo;
drop table if exists app_user cascade;
drop table if exists user_role cascade;
drop sequence if exists global_seq;

CREATE SEQUENCE global_seq START 100000;

/*==============================================================*/
/* Table: app_user                                              */
/*==============================================================*/
create table app_user
(
    id       BIGINT PRIMARY KEY DEFAULT nextval('global_seq'),
    username VARCHAR(60) UNIQUE NOT NULL,
    password VARCHAR(60)        NOT NULL,
    active   BOOLEAN            NOT NULL DEFAULT TRUE
);

comment on column app_user.id is
    'Идентификатор пользователя';

comment on column app_user.username is
    'Имя пользователя';

comment on column app_user.password is
    'Пароль пользователя в зашифрованном виде';

comment on column app_user.active is
    'Признак пользователь активирован';

/*==============================================================*/
/* Table: user_role                                             */
/*==============================================================*/

create table user_role
(
    user_id BIGINT REFERENCES app_user (id) ON DELETE CASCADE,
    roles   VARCHAR(255) NOT NULL,
    CONSTRAINT user_roles_idx UNIQUE (user_id, roles)
);

comment on column user_role.user_id is
    'Идентификатор пользователя';

comment on column user_role.roles is
    'Роли пользователя';