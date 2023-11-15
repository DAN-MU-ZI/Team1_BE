package com.example.team1_be.domain.Apply.Service;

import com.example.team1_be.domain.Week.Week;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Apply.ApplyStatus;
import com.example.team1_be.domain.DetailWorktime.DetailWorktime;
import com.example.team1_be.domain.User.User;
import com.example.team1_be.domain.Worktime.Worktime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplyService {
    private final ApplyReadOnlyService readOnlyService;
    private final ApplyWriteOnlyService writeOnlyService;

    public List<Apply> findApplies(List<Worktime> worktimes) {
        List<Long> worktimeIds = worktimes.stream()
                .map(Worktime::getId)
                .collect(Collectors.toList());
        log.info("{}개의 근무 시간에 대한 신청 정보를 조회합니다.", worktimeIds.size());
        return readOnlyService.findApplyByWorktimeIds(worktimeIds);
    }

    public void registerApplies(List<Apply> applies) {
        log.info("{}개의 신청 정보를 생성합니다.", applies.size());
        writeOnlyService.registerAppliesForUser(applies);
    }

    private SortedMap<LocalDate, List<Apply>> findAppliesByStatusAndDate(
            SortedMap<LocalDate, List<DetailWorktime>> monthlyDetailWorktimes, User user, ApplyStatus status) {
        SortedMap<LocalDate, List<Apply>> monthlyApplies = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : monthlyDetailWorktimes.keySet()) {
            List<Apply> applies = readOnlyService.findApplyByUserIdAndDateAndStatus(user.getId(), date, status);
            monthlyApplies.put(date, applies);
        }
        return monthlyApplies;
    }

    public SortedMap<LocalDate, List<Apply>> findFixedAppliesByUserAndDate(
            SortedMap<LocalDate, List<DetailWorktime>> monthlyDetailWorktimes, User user) {
        SortedMap<LocalDate, List<Apply>> monthlyApplies = findAppliesByStatusAndDate(monthlyDetailWorktimes, user,
                ApplyStatus.FIX);
        return monthlyApplies;
    }

    public List<User> findUsersByWorktimeAndStatus(DetailWorktime worktime, ApplyStatus status) {
        List<User> users = readOnlyService.findUsersByWorktimeIdAndApplyStatus(worktime.getId(), status);
        if (users.isEmpty()) {
            log.warn("근무 시간 ID: {}, 상태: {}에 따른 신청자를 찾을 수 없습니다.", worktime.getId(), status);
        }
        log.info("근무 시간 ID: {}, 상태: {}에 따른 신청자를 조회하였습니다.", worktime.getId(), status);
        return users;
    }

    public List<User> findUsersByWorktimeAndFixedStatus(DetailWorktime worktime) {
        return findUsersByWorktimeAndStatus(worktime, ApplyStatus.FIX);
    }

    public SortedMap<LocalDate, List<Apply>> findFixedPersonalApplies(
            SortedMap<LocalDate, List<DetailWorktime>> monthlyDetailWorktimes, User user) {
        return findAppliesByStatusAndDate(monthlyDetailWorktimes, user, ApplyStatus.FIX);
    }

    public List<SortedMap<Worktime, Apply>> findWeeklyAppliesByUser(User user,
                                                                    List<Worktime> weeklyWorktimes) {
        List<SortedMap<Worktime, Apply>> weeklyApplies = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            SortedMap<Worktime, Apply> dailyApplies = new TreeMap<>((s1, s2) -> s1.getId().compareTo(s2.getId()));
            for (Worktime worktime : weeklyWorktimes) {
                dailyApplies.put(worktime,
                        readOnlyService.findApplyByUserIdAndWorktimeIdAndDay(user.getId(), worktime.getId(), day));
            }
            weeklyApplies.add(dailyApplies);
        }
        return weeklyApplies;
    }

    public void updateApplies(User user, List<DetailWorktime> previousDetailWorktimes,
                              List<DetailWorktime> appliedDetailWorktimes) {
        HashSet<DetailWorktime> previousDetailWorktimeSet = new HashSet<>(previousDetailWorktimes);
        HashSet<DetailWorktime> appliedDetailWorktimeSet = new HashSet<>(appliedDetailWorktimes);

        deleteApplies(user, previousDetailWorktimeSet);
        createApplies(user, appliedDetailWorktimeSet);
    }

    private void deleteApplies(User user, HashSet<DetailWorktime> detailWorktimes) {
        List<Long> detailWorktimeIds = detailWorktimes.stream()
                .map(DetailWorktime::getId)
                .collect(Collectors.toList());
        List<Apply> appliesToDelete = readOnlyService.findApplyByUserIdAndDetailWorktimeIds(user.getId(),
                detailWorktimeIds);
        log.info("사용자 ID: {}, 상세 근무 시간 ID: {}에 따른 신청 정보 {} 개를 삭제합니다.", user.getId(), detailWorktimeIds,
                appliesToDelete.size());
        writeOnlyService.deleteApplies(appliesToDelete);
    }

    private void createApplies(User user, HashSet<DetailWorktime> detailWorktimes) {
        log.info("사용자 ID: {}에 대하여 신청 정보 {} 개를 생성합니다.", user.getId(), detailWorktimes.size());
        writeOnlyService.registerAppliesForUser(user, new ArrayList<>(detailWorktimes));
    }

    public List<Worktime> findFixedWorktimeAtCurrentWeek(Week week, User user, Boolean isPersonal) {
        if (isPersonal) {
            List<Apply> applies = readOnlyService.findFixedAppliesByUserAndWeek(user.getId(), week.getId());
            return getWorktimes(applies);
        }

        List<Apply> applies = readOnlyService.findFixedAppliesByWeek(week.getId());
        return getWorktimes(applies);
    }

    private static List<Worktime> getWorktimes(List<Apply> applies) {
        return applies.stream().map(Apply::getDetailWorktime).map(DetailWorktime::getWorktime)
                .collect(Collectors.toList());
    }
}
