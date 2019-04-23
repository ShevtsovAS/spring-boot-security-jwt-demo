insert into app_users(id, username, password)
values (1, 'user', '$2a$10$wschGsPiIyObJxCRRQRMSesPO73pcXVkKB/KoA4QP4WCZ9TKNOALG'),
       (2, 'admin', '$2a$10$OFSGwA7AYQMVHuymqBEi/OLTWc/gsIGv0Tx2rNJDflPHpCSJSVEny');

insert into roles(id, role_name)
values (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

insert into user_roles(user_id, role_id)
values (1, 1),
       (2, 1),
       (2, 2);
