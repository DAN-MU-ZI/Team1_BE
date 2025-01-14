package com.example.team1_be.domain.DetailWorktime.Service;

import com.example.team1_be.domain.Week.Week;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Apply.ApplyStatus;
import com.example.team1_be.domain.DetailWorktime.DetailWorktime;
import com.example.team1_be.domain.Group.Group;
import com.example.team1_be.domain.Week.WeekRecruitmentStatus;
import com.example.team1_be.domain.Worktime.Worktime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DetailWorktimeService {
    private final DetailWorktimeReadOnlyService readOnlyService;
    private final DetailWorktimeWriteOnlyService writeOnlyService;

    public void createDays(List<DetailWorktime> days) {
        writeOnlyService.registerDetailWorktimes(days);
        log.info("{}개의 상세 근무 시간이 생성되었습니다.", days.size());
    }

    public void createDays(LocalDate week, List<Worktime> weeklyWorktimes, List<List<Long>> amount) {
        writeOnlyService.registerWeeklyDetailWorktimesWithWorktimesAndAmounts(week, weeklyWorktimes, amount);
    }

    public TreeMap<String, List<Map<Worktime, List<Apply>>>> findAppliesByWorktimeAndDayAndStatus(
            List<Worktime> worktimes,
            ApplyStatus status) {
        TreeMap<String, List<Map<Worktime, List<Apply>>>> weeklyApplies = new TreeMap<>(
                Comparator.comparing(DayOfWeek::valueOf));
        for (DayOfWeek day : DayOfWeek.values()) {
            List<Map<Worktime, List<Apply>>> dailyApplies = new ArrayList<>();
            for (Worktime worktime : worktimes) {
                dailyApplies.add(findAppliesByWorktimeAndStatusForDay(worktime, status, day));
            }
            weeklyApplies.put(day.toString(), dailyApplies);
        }
        log.info("일주일 동안의 신청 정보를 조회하였습니다.");
        return weeklyApplies;
    }

    private Map<Worktime, List<Apply>> findAppliesByWorktimeAndStatusForDay(Worktime worktime, ApplyStatus status,
                                                                            DayOfWeek day) {
        List<Apply> applies = readOnlyService.findAppliesByWorktimeDayAndStatus(worktime, status, day);
        log.info("근무 시간 ID: {}, 요일: {}에 따른 신청 정보를 조회하였습니다.", worktime.getId(), day);
        Map<Worktime, List<Apply>> appliesByWorktime = new HashMap<>();
        appliesByWorktime.put(worktime, applies);
        return appliesByWorktime;
    }

    public SortedMap<LocalDate, List<DetailWorktime>> findEndedByGroupAndYearMonth(Group group, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        SortedMap<LocalDate, List<DetailWorktime>> monthlyDetailsWorktimesMap = new TreeMap<>(
                Comparator.naturalOrder());
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<DetailWorktime> detailWorktimes = readOnlyService.findDetailWorktimesByGroupDateAndStatus(group, date,
                    WeekRecruitmentStatus.ENDED);

            if (detailWorktimes.isEmpty()) {
                continue;
            }

            monthlyDetailsWorktimesMap.put(date, detailWorktimes);
        }

//		if (monthlyDetailsWorktimesMap.isEmpty()) {
//			log.warn("확정된 스케줄이 없습니다.");
//			throw new NotFoundException("확정된 스케줄이 없습니다.");
//		}
        log.info("그룹 ID: {}, 년월: {}에 따른 종료된 상세 근무 시간 정보를 조회하였습니다.", group.getId(), yearMonth);
        return monthlyDetailsWorktimesMap;
    }

    public List<DetailWorktime> findByStartDateAndGroup(LocalDate date, Group group) {
        return readOnlyService.findDetailWorktimesByStartDateAndGroup(date, group);
    }

    public List<DetailWorktime> findByGroupAndDate(Group group, LocalDate selectedDate) {
        return readOnlyService.findDetailWorktimesByGroupAndDate(group, selectedDate);
    }

    public List<DetailWorktime> findByStartDateAndWorktimes(LocalDate date, List<Worktime> worktimes) {
        List<Long> ids = worktimes.stream().map(Worktime::getId).collect(
                Collectors.toList());
        return readOnlyService.findDetailWorktimesByStartDateAndWorktimes(date, ids);
    }

    public List<DetailWorktime> findByStartDateAndWorktimes(SortedMap<DayOfWeek, List<Worktime>> weeklyApplies) {
        List<DetailWorktime> appliedDetailWorktimes = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<Long> worktimeIds = weeklyApplies.get(day).stream().map(Worktime::getId).collect(Collectors.toList());
            if (!worktimeIds.isEmpty()) {
                appliedDetailWorktimes.addAll(readOnlyService.findDetailWorktimesByDayAndWorktimeIds(day, worktimeIds));
            }
        }
        return appliedDetailWorktimes;
    }

    public TreeMap<LocalDate, List<DetailWorktime>> findByWeekOrNull(Group group, Week week) {
        TreeMap<LocalDate, List<DetailWorktime>> result = new TreeMap<>();
        LocalDate startDate = week.getStartDate();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            LocalDate targetDate = startDate.plusDays(dayOfWeek.ordinal());
            result.put(targetDate, readOnlyService.findDetailWorktimesByStartDateAndGroupOrNull(targetDate, group));
        }
        return result;
    }
}
