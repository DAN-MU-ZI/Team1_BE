SET
REFERENTIAL_INTEGRITY FALSE;
truncate table apply;
ALTER TABLE apply AUTO_INCREMENT=1;

truncate table detail_worktime;
ALTER TABLE detail_worktime AUTO_INCREMENT=1;

truncate table groups;
ALTER TABLE groups AUTO_INCREMENT=1;

truncate table invite;
ALTER TABLE invite AUTO_INCREMENT=1;

truncate table notification;
ALTER TABLE notification AUTO_INCREMENT=1;

truncate table recommended_weekly_schedule;
ALTER TABLE recommended_weekly_schedule AUTO_INCREMENT=1;

truncate table recommended_worktime_apply;
ALTER TABLE recommended_worktime_apply AUTO_INCREMENT=1;

truncate table substitute;
ALTER TABLE substitute AUTO_INCREMENT=1;

truncate table unfinished_user;
ALTER TABLE unfinished_user AUTO_INCREMENT=1;

truncate table users;
ALTER TABLE users AUTO_INCREMENT=1;

truncate table week;
ALTER TABLE week AUTO_INCREMENT=1;

truncate table worktime;
ALTER TABLE worktime AUTO_INCREMENT=1;

truncate table roles;
ALTER TABLE roles AUTO_INCREMENT=1;

SET
REFERENTIAL_INTEGRITY TRUE;


insert into `groups` (id, name, phone_number, business_number, address)
values (1, '맘스터치', '011-0000-0001', 1, '부산광역시');

insert into `users` (id, kakao_id, name, phone_number, is_admin, group_id)
values (1, 3040993001, '이재훈', '010-0000-0001', true, 1),
       (2, 2, '무지', '010-0000-0007', false, 1),
       (3, 3, '라이언', '010-0000-0004', false, 1),
       (4, 4, '어피치', '010-0000-0004', false, 1);

insert into roles (`id`, `role_type`, `user_id`)
values (1, 'ROLE_ADMIN', 1),
       (2, 'ROLE_MEMBER', 2),
       (3, 'ROLE_MEMBER', 3),
       (4, 'ROLE_MEMBER', 4);

insert into invite (`id`, `code`, `group_id`)
values (1, 'testcode1', 1);

insert into week(`id`, `status`, `start_date`, `group_id`)
values (1, 'STARTED', '2023-12-04', 1);

insert into worktime(`id`, `title`, `start_time`, `end_time`, `week_id`)
values (1, '오픈', '00:00:00', '06:00:00', 1),
       (2, '미들', '07:00:00', '12:00:00', 1),
       (3, '마감', '13:00:00', '18:00:00', 1);

-- monday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (1, '2023-12-04', 0, 3, 1),
       (2, '2023-12-04', 0, 2, 2),
       (3, '2023-12-04', 0, 1, 3);

-- tuesday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (4, '2023-12-05', 1, 2, 1),
       (5, '2023-12-05', 1, 2, 2),
       (6, '2023-12-05', 1, 2, 3);

-- wednesday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (7, '2023-12-06', 2, 3, 1),
       (8, '2023-12-06', 2, 3, 2),
       (9, '2023-12-06', 2, 0, 3);

-- thursday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (10, '2023-12-07', 3, 1, 1),
       (11, '2023-12-07', 3, 1, 2),
       (12, '2023-12-07', 3, 1, 3);

-- friday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (13, '2023-12-08', 4, 1, 1),
       (14, '2023-12-08', 4, 2, 2),
       (15, '2023-12-08', 4, 1, 3);

-- saturday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (16, '2023-12-09', 5, 1, 1),
       (17, '2023-12-09', 5, 1, 2),
       (18, '2023-12-09', 5, 0, 3);

-- sunday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (19, '2023-12-10', 6, 2, 1),
       (20, '2023-12-10', 6, 2, 2),
       (21, '2023-12-10', 6, 0, 3);

-- 1st member's applies
insert into apply (`id`, `status`, `user_id`, `detail_worktime_id`)
values (1, 'REMAIN', 2, 1),
       (2, 'REMAIN', 2, 2),
       (3, 'REMAIN', 2, 3),
       (4, 'REMAIN', 2, 4),
       (5, 'REMAIN', 2, 5),
       (6, 'REMAIN', 2, 6),
       (7, 'REMAIN', 2, 7),
       (8, 'REMAIN', 2, 8),
       (9, 'REMAIN', 2, 9),
       (10, 'REMAIN', 2, 10),
       (11, 'REMAIN', 2, 11),
       (12, 'REMAIN', 2, 12),
       (13, 'REMAIN', 2, 13),
       (14, 'REMAIN', 2, 14),
       (15, 'REMAIN', 2, 15),
       (16, 'REMAIN', 2, 16),
       (17, 'REMAIN', 2, 17),
       (18, 'REMAIN', 2, 18),
       (19, 'REMAIN', 2, 19),
       (20, 'REMAIN', 2, 20),
       (21, 'REMAIN', 2, 21);

-- 1st member's applies
insert into apply (`id`, `status`, `user_id`, `detail_worktime_id`)
values (22, 'REMAIN', 3, 1),
       (23, 'REMAIN', 3, 2),
       (24, 'REMAIN', 3, 3),
       (25, 'REMAIN', 3, 4),
       (26, 'REMAIN', 3, 5),
       (27, 'REMAIN', 3, 6),
       (28, 'REMAIN', 3, 7),
       (29, 'REMAIN', 3, 8),
       (30, 'REMAIN', 3, 9),
       (31, 'REMAIN', 3, 10),
       (32, 'REMAIN', 3, 11),
       (33, 'REMAIN', 3, 12),
       (34, 'REMAIN', 3, 13),
       (35, 'REMAIN', 3, 14),
       (36, 'REMAIN', 3, 15),
       (37, 'REMAIN', 3, 16),
       (38, 'REMAIN', 3, 17),
       (39, 'REMAIN', 3, 18),
       (40, 'REMAIN', 3, 19),
       (41, 'REMAIN', 3, 20),
       (42, 'REMAIN', 3, 21);

insert into apply (`id`, `status`, `user_id`, `detail_worktime_id`)
values (43, 'REMAIN', 4, 1),
       (44, 'REMAIN', 4, 7),
       (45, 'REMAIN', 4, 8),
       (46, 'REMAIN', 4, 3);
