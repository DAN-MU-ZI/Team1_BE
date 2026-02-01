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


insert into groups (`id`, `name`, `phone_number`, `business_number`, `address`)
values (1, '백소정 부산대점', '011-0000-0001', 1, '부산광역시');

insert into users (`id`, `kakao_id`, `name`, `phone_number`, `is_admin`, `group_id`)
values (1, 1, '이재훈', '010-0000-0001', true, 1),
       (2, 2, '안한주', '010-0000-0002', false, 1);

insert into roles (`id`, `role_type`, `user_id`)
values (1, 'ROLE_ADMIN', 1),
       (2, 'ROLE_MEMBER', 2);


insert into invite (`id`, `code`, `group_id`)
values (1, 'testcode1', 1);

insert into week(`id`, `status`, `start_date`, `group_id`)
values (1, 'ENDED', '2023-11-20', 1);

insert into worktime(`id`, `title`, `start_time`, `end_time`, `week_id`)
values (1, '오픈', '00:00:00', '06:00:00', 1);

-- -- 1st week schedule
-- monday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (1, '2023-11-20', 0, 3, 1);

-- tuesday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (2, '2023-11-21', 1, 2, 1);

-- wednesday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (3, '2023-11-22', 2, 3, 1);

-- thursday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (4, '2023-11-23', 3, 1, 1);

-- friday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (5, '2023-11-24', 4, 1, 1);

-- saturday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (6, '2023-11-25', 5, 1, 1);

-- sunday
insert into detail_worktime(`id`, `date`, `day_of_week`, `amount`, `worktime_id`)
values (7, '2023-11-26', 6, 2, 1);

insert into apply (`id`, `status`, `user_id`, `detail_worktime_id`)
values (1, 'FIX', 2, 1),
       (2, 'FIX', 2, 2);