package com.example.team1_be.domain.Substitute;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Apply.ApplyRepository;
import com.example.team1_be.domain.Apply.ApplyType;
import com.example.team1_be.domain.Day.Day;
import com.example.team1_be.domain.Day.DayRepository;
import com.example.team1_be.domain.Day.Weekday;
import com.example.team1_be.domain.Group.Group;
import com.example.team1_be.domain.Group.GroupRepository;
import com.example.team1_be.domain.Member.Member;
import com.example.team1_be.domain.Member.MemberRepository;
import com.example.team1_be.domain.Schedule.Schedule;
import com.example.team1_be.domain.Schedule.ScheduleRepository;
import com.example.team1_be.domain.User.User;
import com.example.team1_be.domain.User.UserRepository;
import com.example.team1_be.domain.Week.Week;
import com.example.team1_be.domain.Week.WeekRepository;
import com.example.team1_be.domain.Worktime.Worktime;
import com.example.team1_be.domain.Worktime.WorktimeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SubstitueRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private WeekRepository weekRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private WorktimeRepository worktimeRepository;
    @Autowired
    private ApplyRepository applyRepository;
    @Autowired
    private SubstituteRepository substituteRepository;
    @Autowired
    private EntityManager em;

    @AfterEach
    public void resetRepository() {
        em.clear();

        substituteRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Substitute_tb ALTER COLUMN `substitute_id` RESTART WITH 1")
                .executeUpdate();

        applyRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Apply_tb ALTER COLUMN `apply_id` RESTART WITH 1")
                .executeUpdate();

        worktimeRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Worktime_tb ALTER COLUMN `worktime_id` RESTART WITH 1")
                .executeUpdate();

        dayRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Day_tb ALTER COLUMN `day_id` RESTART WITH 1")
                .executeUpdate();

        weekRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Week_tb ALTER COLUMN `week_id` RESTART WITH 1")
                .executeUpdate();

        scheduleRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Schedule_tb ALTER COLUMN `schedule_id` RESTART WITH 1")
                .executeUpdate();

        memberRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Member_tb ALTER COLUMN `member_id` RESTART WITH 1")
                .executeUpdate();

        groupRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE Group_tb ALTER COLUMN `group_id` RESTART WITH 1")
                .executeUpdate();

        userRepository.deleteAll();
        em.createNativeQuery("ALTER TABLE User_tb ALTER COLUMN `user_id` RESTART WITH 1")
                .executeUpdate();

        em.clear();
    }

    @DisplayName("대타 신청서를 생성할 수 있다.")
    @Test
    void test1() {
        Substitute.builder()
                .content("이런 사유로 대타 신청합니다.")
                .adminApprove(false)
                .build();
    }

    @DisplayName("대타 신청서를 저장할 수 있다.")
    @Test
    void test2() {
        Group group = Group.builder()
                .address("부산광역시")
                .name("맘스터치")
                .phoneNumber("010-2222-2222")
                .build();
        groupRepository.save(group);

        Schedule schedule = Schedule.builder()
                .group(group)
                .build();
        scheduleRepository.save(schedule);

        Week week = Week.builder()
                .schedule(schedule)
                .startTime(LocalDateTime.now())
                .build();
        weekRepository.save(week);

        Day day = Day.builder()
                .week(week)
                .weekday(Weekday.Monday)
                .build();
        dayRepository.save(day);

        Worktime worktime = Worktime.builder()
                .day(day)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
        worktimeRepository.save(worktime);

        User user = User.builder()
                .name("이재훈")
                .phoneNumber("010-5538-6818")
                .build();
        userRepository.save(user);

        Member member = Member.builder()
                .user(user)
                .isAdmin(false)
                .group(group)
                .build();
        memberRepository.save(member);

        Apply apply = Apply.builder()
                .state(ApplyType.REMAIN)
                .worktime(worktime)
                .member(member)
                .build();
        applyRepository.save(apply);

        Substitute substitute = Substitute.builder()
                .content("이런 사유로 대타 신청합니다.")
                .adminApprove(false)
                .applicant(apply)
                .build();
        substituteRepository.save(substitute);
    }

    @DisplayName("대타 신청서를 조회할 수 있다.")
    @Test
    void test3() {
        Group group = Group.builder()
                .address("부산광역시")
                .name("맘스터치")
                .phoneNumber("010-2222-2222")
                .build();
        groupRepository.save(group);

        Schedule schedule = Schedule.builder()
                .group(group)
                .build();
        scheduleRepository.save(schedule);

        Week week = Week.builder()
                .schedule(schedule)
                .startTime(LocalDateTime.now())
                .build();
        weekRepository.save(week);

        Day day = Day.builder()
                .week(week)
                .weekday(Weekday.Monday)
                .build();
        dayRepository.save(day);

        Worktime worktime = Worktime.builder()
                .day(day)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
        worktimeRepository.save(worktime);

        User user = User.builder()
                .name("이재훈")
                .phoneNumber("010-5538-6818")
                .build();
        userRepository.save(user);

        Member member = Member.builder()
                .user(user)
                .isAdmin(false)
                .group(group)
                .build();
        memberRepository.save(member);

        Apply apply = Apply.builder()
                .state(ApplyType.REMAIN)
                .worktime(worktime)
                .member(member)
                .build();
        applyRepository.save(apply);

        Substitute substitute = Substitute.builder()
                .content("이런 사유로 대타 신청합니다.")
                .adminApprove(false)
                .applicant(apply)
                .build();
        substituteRepository.save(substitute);

        assertThat(substituteRepository.findById(1)
                .orElse(null))
                .isNotEqualTo(null);
    }
}