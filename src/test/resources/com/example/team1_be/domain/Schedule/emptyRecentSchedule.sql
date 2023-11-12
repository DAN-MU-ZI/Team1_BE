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
       (2, 2, '안한주', '010-0000-0002', false, 1),
       (3, 3, '차지원', '010-0000-0003', false, 1),
       (4, 4, '최은진', '010-0000-0004', false, 1),
       (5, 5, '이현지', '010-0000-0005', false, 1),
       (6, 6, '민하린', '010-0000-0006', false, 1),
       (7, 7, '홍길동', '010-0000-0007', false, 1);

insert into roles (`id`, `role_type`, `user_id`)
values (1, 'ROLE_ADMIN', 1),
       (2, 'ROLE_MEMBER', 2),
       (3, 'ROLE_MEMBER', 3),
       (4, 'ROLE_MEMBER', 4),
       (5, 'ROLE_MEMBER', 5),
       (6, 'ROLE_MEMBER', 6),
       (7, 'ROLE_MEMBER', 7);


insert into invite (`id`, `code`, `group_id`)
values (1, 'testcode1', 1);

INSERT INTO notification (`id`, `content`, `type`, `is_read`, `user_id`, `created_by`, `created_at`, `last_updated_by`,
                          `updated_at`)
VALUES (1, 'ㅁㅁㅁ 님! 새로운 모임을 만들어보세요~', 'START', false, 3, 1, '2022-11-22 12:34:56', 1, '2022-11-22 12:34:56'),
       (2, 'ㅇㅇㅇ 님! 새로운 알림입니다.', 'START', true, 4, 1, '2023-10-13 10:00:00', 1, '2023-10-13 10:00:00'),
       (3, 'ㅇㅇㅇ 님! 새로운 알림입니다.', 'START', true, 3, 1, '2023-10-13 10:00:00', 1, '2023-10-13 10:00:00'),
       (4, 'ㅁㅁ 님! 새로운 모임을 만들어보세요~', 'START', false, 2, 1, '2023-10-13 10:00:00', 1, '2023-10-13 10:00:00');