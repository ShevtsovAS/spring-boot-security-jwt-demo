insert into app_user(id, username, password)
values (1, 'user', '$2a$10$wschGsPiIyObJxCRRQRMSesPO73pcXVkKB/KoA4QP4WCZ9TKNOALG'), -- password: password
       (2, 'admin', '$2a$10$OFSGwA7AYQMVHuymqBEi/OLTWc/gsIGv0Tx2rNJDflPHpCSJSVEny'); -- password: admin

insert into user_role(user_id, roles)
values (1, 'USER'),
       (2, 'USER'),
       (2, 'ADMIN');
