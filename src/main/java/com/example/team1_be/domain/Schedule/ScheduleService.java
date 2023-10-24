package com.example.team1_be.domain.Schedule;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Apply.ApplyRepository;
import com.example.team1_be.domain.Apply.ApplyStatus;
import com.example.team1_be.domain.Day.Day;
import com.example.team1_be.domain.Day.DayRepository;
import com.example.team1_be.domain.Group.Group;
import com.example.team1_be.domain.Group.GroupService;
import com.example.team1_be.domain.Member.Member;
import com.example.team1_be.domain.Member.MemberRepository;
import com.example.team1_be.domain.Member.MemberService;
import com.example.team1_be.domain.Schedule.DTO.*;
import com.example.team1_be.domain.Schedule.Recommend.*;
import com.example.team1_be.domain.User.User;
import com.example.team1_be.domain.Week.Week;
import com.example.team1_be.domain.Week.WeekRecruitmentStatus;
import com.example.team1_be.domain.Week.WeekService;
import com.example.team1_be.domain.Worktime.Worktime;
import com.example.team1_be.domain.Worktime.WorktimeRepository;
import com.example.team1_be.utils.errors.exception.BadRequestException;
import com.example.team1_be.utils.errors.exception.CustomException;
import com.example.team1_be.utils.errors.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final int NUM_DAYS_OF_WEEK = 7;

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final GroupService groupService;
    private final ScheduleRepository scheduleRepository;
    private final WeekService weekService;
    private final DayRepository dayRepository;
    private final WorktimeRepository worktimeRepository;
    private final ApplyRepository applyRepository;
    private final RecommendedWorktimeApplyRepository recommendedWorktimeApplyRepository;
    private final RecommendedWeeklyScheduleRepository recommendedWeeklyScheduleRepository;

    @Transactional
    public void recruitSchedule(User user, RecruitSchedule.Request request) {
        if (request.getWeeklyAmount().size() != NUM_DAYS_OF_WEEK) {
            throw new CustomException("모든 요일에 대한 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // group 찾기
        Group group = groupService.findByUser(user);

        // 스케줄 생성
        Schedule schedule = Schedule.builder()
                .group(group)
                .build();
        scheduleRepository.save(schedule);

        // week 생성
        Week week = weekService.createWeek(schedule, request.getWeekStartDate());

        // 각 요일 정보(Day) 기입
        List<RecruitSchedule.Request.DailySchedule> weeklyAmount = request.getWeeklyAmount();
        List<Day> days = IntStream.range(1, weeklyAmount.size() + 1)
                .mapToObj(dayOfWeek -> Day.builder()
                        .week(week)
                        .dayOfWeek(dayOfWeek)
                        .build()
                ).collect(Collectors.toList());
        dayRepository.saveAll(days);

        List<Worktime> worktimeList = new ArrayList<>();
        IntStream.range(0, days.size())
                .forEach(dayIdx -> weeklyAmount.get(dayIdx)
                        .getDailySchedules()
                        .forEach(worktime -> worktimeList.add(Worktime.builder()
                                .title(worktime.getTitle())
                                .startTime(worktime.getStartTime())
                                .endTime(worktime.getEndTime())
                                .amount(worktime.getAmount())
                                .day(days.get(dayIdx))
                                .build())));
        worktimeRepository.saveAll(worktimeList);
    }

    public WeeklyScheduleCheck.Response weeklyScheduleCheck(User user, LocalDate request) {
        Group group = groupService.findByUser(user);

        Schedule schedule = findByGroup(group);

        Week week = user.getIsAdmin() ?
                weekService.findByScheduleIdStartDateAndStatus(schedule, request, WeekRecruitmentStatus.STARTED) :
                weekService.findByScheduleIdStartDateAndStatus(schedule, request, WeekRecruitmentStatus.ENDED);

        List<Day> days = dayRepository.findByWeekId(week.getId());
        if (days.size() == 0) {
            throw new CustomException("잘못된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<List<Worktime>> weeklyWorktime = days.stream().map(day -> worktimeRepository.findByDayId(day.getId())).collect(Collectors.toList());
        List<List<List<Apply>>> applyList = weeklyWorktime.stream()
                .map(worktimes -> worktimes.stream()
                        .map(worktime -> applyRepository.findAppliesByWorktimeId(worktime.getId()))
                        .collect(Collectors.toList())).collect(Collectors.toList());

        return new WeeklyScheduleCheck.Response(weeklyWorktime, applyList);
    }

    public GetFixedWeeklySchedule.Response getFixedWeeklySchedule(User user, YearMonth requestMonth, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("유효하지 않은 요청", HttpStatus.BAD_REQUEST));
        Schedule schedule = findByGroup(member.getGroup());

        LocalDate date = LocalDate.of(requestMonth.getYear(), requestMonth.getMonth(), 1);
        LocalDate toDate = LocalDate.of(requestMonth.getYear(), requestMonth.getMonth(), 1).plusMonths(1);
        List<Week> weeks = weekService.findByScheduleAndYearMonthAndStatus(date, toDate, schedule, WeekRecruitmentStatus.ENDED);
        List<Worktime> memberWorktimes = applyRepository.findByYearMonthAndStatusAndMemberId(date, toDate, member.getId(), ApplyStatus.FIX);
        Double monthly = memberWorktimes.stream()
                .mapToDouble(worktime -> Duration.between(worktime.getStartTime(), worktime.getEndTime()).getSeconds() / 3600)
                .reduce(0D, Double::sum);

        return new GetFixedWeeklySchedule.Response(memberWorktimes, monthly, monthly / weeks.size());
    }

    @Transactional
    public RecommendSchedule.Response recommendSchedule(User user, LocalDate date) {
        Group group = groupService.findByUser(user);

        Schedule schedule = findByGroup(group);

        List<Worktime> weeklyWorktimes = worktimeRepository.findByStartDateAndScheduleId(date, schedule.getId());
        if (weeklyWorktimes.size() == 0) {
            throw new CustomException("등록된 근무일정이 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<Long> worktimeIds = weeklyWorktimes.stream()
                .map(Worktime::getId)
                .collect(Collectors.toList());

        List<Apply> applyList = applyRepository.findAppliesByWorktimeIds(worktimeIds);

        Map<Long, Integer> requestMap = weeklyWorktimes.stream()
                .collect(Collectors.toMap(Worktime::getId, Worktime::getAmount));

        SchduleGenerator generator = new SchduleGenerator(applyList, requestMap);
        List<List<Apply>> generatedSchedules = generator.generateSchedule();

        for (List<Apply> generatedSchedule : generatedSchedules) {
            RecommendedWeeklySchedule weeklySchedule = RecommendedWeeklySchedule.builder()
                    .user(user)
                    .build();
            recommendedWeeklyScheduleRepository.save(weeklySchedule);

            List<RecommendedWorktimeApply> recommendedWorktimeApplies = new ArrayList<>();
            for (Worktime worktime : weeklyWorktimes) {
                List<Apply> applies = generatedSchedule.stream()
                        .filter(x -> x.getWorktime().getId().equals(worktime.getId()))
                        .collect(Collectors.toList());

                for (Apply apply : applies) {
                    recommendedWorktimeApplies.add(RecommendedWorktimeApply.builder()
                            .recommendedWeeklySchedule(weeklySchedule)
                            .apply(apply)
                            .build());
                }
            }

            recommendedWorktimeApplyRepository.saveAll(recommendedWorktimeApplies);
        }
        return new RecommendSchedule.Response(weeklyWorktimes, generatedSchedules);
    }

    @Transactional
    public void fixSchedule(User user, FixSchedule.Request request) {
        List<RecommendedWeeklySchedule> recommendedSchedule = recommendedWeeklyScheduleRepository.findByUser(user.getId());
        RecommendedWeeklySchedule recommendedWeeklySchedule = recommendedSchedule.get(request.getSelection());

        Week week = recommendedWeeklySchedule.getRecommendedWorktimeApplies().get(0).getApply().getWorktime().getDay().getWeek();

        weekService.updateWeekStatus(week, WeekRecruitmentStatus.ENDED);

        List<Apply> selectedApplies = new ArrayList<>();
        recommendedWeeklySchedule.getRecommendedWorktimeApplies()
                .forEach(recommendedWorktimeApply ->
                        selectedApplies.add(recommendedWorktimeApply.getApply().updateStatus(ApplyStatus.FIX)));
        applyRepository.saveAll(selectedApplies);


        recommendedSchedule.forEach(x -> recommendedWorktimeApplyRepository.deleteAll(x.getRecommendedWorktimeApplies()));
        recommendedWeeklyScheduleRepository.deleteAll(recommendedSchedule);
    }

    public GetDailyFixedApplies.Response getDailyFixedApplies(User user, LocalDate selectedDate) {
        Group group = groupService.findByUser(user);
        Schedule schedule = findByGroup(group);

        LocalDate date = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1);
        int dayOfWeek = selectedDate.getDayOfWeek().getValue();
        List<Worktime> worktimes = worktimeRepository.findBySpecificDateAndScheduleId(date, dayOfWeek, schedule.getId());
        if (worktimes.isEmpty()) {
            throw new BadRequestException("확정된 스케줄이 아닙니다.");
        }

        List<List<Apply>> dailyApplies = new ArrayList<>();
        for (Worktime worktime : worktimes) {
            List<Apply> applies = applyRepository.findFixedAppliesByWorktimeId(worktime.getId());
            if (applies.size() != worktime.getAmount()) {
                throw new NotFoundException("기존 worktime에서 모집하는 인원을 충족하지 못했습니다.");
            }
            dailyApplies.add(applies);
        }
        return new GetDailyFixedApplies.Response(worktimes, dailyApplies);
    }

    public GetFixedWeeklySchedule.Response getUsersFixedWeeklySchedule(User user, YearMonth requestMonth) {
        Member member = memberService.findByUser(user);
        Schedule schedule = findByGroup(member.getGroup());

        LocalDate date = LocalDate.of(requestMonth.getYear(), requestMonth.getMonth(), 1);
        LocalDate toDate = LocalDate.of(requestMonth.getYear(), requestMonth.getMonth(), 1).plusMonths(1);
        List<Week> weeks = weekService.findByScheduleAndYearMonthAndStatus(date, toDate, schedule, WeekRecruitmentStatus.ENDED);
        List<Worktime> memberWorktimes = applyRepository.findByYearMonthAndStatusAndMemberId(date, toDate, member.getId(), ApplyStatus.FIX);
        Double monthly = memberWorktimes.stream()
                .mapToDouble(worktime -> Duration.between(worktime.getStartTime(), worktime.getEndTime()).getSeconds() / 3600)
                .reduce(0D, Double::sum);

        return new GetFixedWeeklySchedule.Response(memberWorktimes, monthly, monthly / weeks.size());
    }

    public LoadLatestSchedule.Response loadLatestSchedule(User user, LocalDate startWeekDate) {
        Group group = groupService.findByUser(user);

        Schedule schedule = findByGroup(group);

        List<Week> latestWeeks = weekService.findLatestByScheduleAndStatus(schedule, WeekRecruitmentStatus.ENDED);
        if (latestWeeks.isEmpty()) {
            throw new NotFoundException("최근 스케줄을 찾을 수 없습니다.");
        }

        List<Worktime> latestWorktimes = latestWeeks.get(0).getDay().get(0).getWorktimes();

        return new LoadLatestSchedule.Response(latestWorktimes);
    }

    public GetWeekStatus.Response getWeekStatus(User user, LocalDate startWeekDate) {
        Group group = groupService.findByUser(user);

        Schedule schedule = findByGroup(group);
        Week week = weekService.findByScheduleAndStartDate(schedule, startWeekDate);

        if (week == null) {
            return new GetWeekStatus.Response(null);
        } else {
            return new GetWeekStatus.Response(week.getStatus());
        }
    }

    public Schedule findByGroup(Group group) {
        return scheduleRepository.findByGroup(group)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));
    }
}
