package com.example.team1_be.domain.Schedule.Recommend;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Apply.ApplyStatus;
import com.example.team1_be.domain.DetailWorktime.DetailWorktime;
import com.example.team1_be.domain.User.User;
import com.example.team1_be.domain.Worktime.Worktime;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleGeneratorLoadTest {

    @Test
    public void runRealisticAlbaLoadTest() {
        // Data Setup: 3 shifts per day, 7 days, 2 people per shift
        List<Worktime> worktimes = new ArrayList<>();
        String[] titles = {"Opening", "Mid", "Closing"};
        LocalTime[] starts = {LocalTime.of(9, 0), LocalTime.of(13, 0), LocalTime.of(18, 0)};
        LocalTime[] ends = {LocalTime.of(13, 0), LocalTime.of(18, 0), LocalTime.of(22, 0)};

        for (int i = 0; i < 3; i++) {
            worktimes.add(Worktime.builder()
                    .id((long) (i + 1))
                    .title(titles[i])
                    .startTime(starts[i])
                    .endTime(ends[i])
                    .week(null)
                    .days(new ArrayList<>())
                    .build());
        }

        List<DetailWorktime> detailWorktimes = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2023, 10, 2);
        long dwIdCounter = 1;
        for (int i = 0; i < 7; i++) {
            for (Worktime wt : worktimes) {
                detailWorktimes.add(DetailWorktime.builder()
                        .id(dwIdCounter++)
                        .date(startDate.plusDays(i))
                        .dayOfWeek(startDate.plusDays(i).getDayOfWeek())
                        .worktime(wt)
                        .amount(2L) // Need 2 people per shift
                        .applies(new ArrayList<>())
                        .build());
            }
        }

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) { // 20 users
            users.add(User.builder()
                    .id((long) (i + 1))
                    .kakaoId(1000L + i)
                    .name("User" + i)
                    .phoneNumber("010-1234-56" + (i < 10 ? "0" + i : i))
                    .isAdmin(false)
                    .roles(Collections.emptySet())
                    .group(null)
                    .applies(new ArrayList<>())
                    .build());
        }

        List<Apply> applyList = new ArrayList<>();
        long applyIdCounter = 1;
        Random random = new Random(42);
        for (User user : users) {
            // Each user randomly applies to 10 slots out of 21
            List<DetailWorktime> shuffled = new ArrayList<>(detailWorktimes);
            Collections.shuffle(shuffled, random);
            for (int i = 0; i < 10; i++) {
                applyList.add(Apply.builder()
                        .id(applyIdCounter++)
                        .detailWorktime(shuffled.get(i))
                        .user(user)
                        .status(ApplyStatus.REMAIN)
                        .recommendedWorktimeApplies(new ArrayList<>())
                        .build());
            }
        }

        Map<Long, Long> requestMap = detailWorktimes.stream()
                .collect(Collectors.toMap(DetailWorktime::getId, DetailWorktime::getAmount));

        // Test
        int iterations = 1000; 
        List<Long> latencies = new ArrayList<>();

        System.out.println("Starting Realistic Alba Load Test... (" + iterations + " iterations)");

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            new ScheduleGenerator(worktimes, applyList, requestMap).generateSchedule();
            long end = System.nanoTime();
            latencies.add(end - start);
        }

        Collections.sort(latencies);

        double p50 = latencies.get((int) (iterations * 0.50)) / 1_000_000.0;
        double p95 = latencies.get((int) (iterations * 0.95)) / 1_000_000.0;
        double p99 = latencies.get((int) (iterations * 0.99)) / 1_000_000.0;

        System.out.println("Realistic Alba Load Test Results (ms):");
        System.out.printf("P50: %.3f ms%n", p50);
        System.out.printf("P95: %.3f ms%n", p95);
        System.out.printf("P99: %.3f ms%n", p99);
    }
}
