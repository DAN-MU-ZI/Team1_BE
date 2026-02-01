package com.example.team1_be.domain.Schedule.Recommend;

import com.example.team1_be.domain.DetailWorktime.DetailWorktime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Worktime.Worktime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleGenerator {
    private final int GEN_LIMIT = 3;
    private final List<Worktime> worktimes;
    private final Map<Long, List<Apply>> appliesBySlotId;
    private final List<DetailWorktime> sortedSlots;
    private final Map<Long, Long> requestMap;
    private final List<List<Apply>> generatedApplies = new ArrayList<>();
    private int remainingLimit;

    public ScheduleGenerator(List<Worktime> worktimes, List<Apply> applyList, Map<Long, Long> requestMap) {
        this.worktimes = worktimes;
        this.requestMap = requestMap;
        this.remainingLimit = GEN_LIMIT;
        
        this.appliesBySlotId = applyList.stream()
                .collect(Collectors.groupingBy(a -> a.getDetailWorktime().getId()));

        // MRV Heuristic: Sort slots by (Available Applicants - Needed Amount) ascending.
        // Difficult slots with fewer applicants are processed first to prune the search space early.
        this.sortedSlots = appliesBySlotId.values().stream()
                .map(list -> list.get(0).getDetailWorktime())
                .filter(dw -> requestMap.getOrDefault(dw.getId(), 0L) > 0)
                .sorted(Comparator.comparingLong(dw -> 
                        appliesBySlotId.get(dw.getId()).size() - requestMap.get(dw.getId())))
                .collect(Collectors.toList());
    }

    public List<Map<DayOfWeek, SortedMap<Worktime, List<Apply>>>> generateSchedule() {
        log.info("스케줄을 생성합니다. (개선된 MRV 알고리즘)");
        
        for (DetailWorktime slot : sortedSlots) {
            long needed = requestMap.get(slot.getId());
            int available = appliesBySlotId.getOrDefault(slot.getId(), new ArrayList<>()).size();
            if (available < needed) {
                log.info("슬롯 {}을 채울 인원이 부족합니다. (필요: {}, 지원: {})", 
                        slot.getId(), needed, available);
                return new ArrayList<>();
            }
        }

        backtrack(0, new ArrayList<>(), new HashSet<>());

        List<Map<DayOfWeek, SortedMap<Worktime, List<Apply>>>> result = this.generatedApplies.stream()
                .map(this::generateDayOfWeekSortedMap)
                .collect(Collectors.toList());

        log.info("스케줄 생성이 완료되었습니다. (생성된 수: {})", result.size());
        return result;
    }

    private void backtrack(int slotIndex, List<Apply> currentFixed, Set<String> userOccupancy) {
        if (remainingLimit == 0) return;

        if (slotIndex == sortedSlots.size()) {
            generatedApplies.add(new ArrayList<>(currentFixed));
            remainingLimit--;
            return;
        }

        DetailWorktime slot = sortedSlots.get(slotIndex);
        long needed = requestMap.get(slot.getId());
        List<Apply> candidates = appliesBySlotId.get(slot.getId());

        fillSlot(slotIndex, (int) needed, 0, candidates, currentFixed, userOccupancy);
    }

    private void fillSlot(int slotIndex, int needed, int startCandidate, List<Apply> candidates, 
                          List<Apply> currentFixed, Set<String> userOccupancy) {
        if (remainingLimit == 0) return;
        if (needed == 0) {
            backtrack(slotIndex + 1, currentFixed, userOccupancy);
            return;
        }

        for (int i = startCandidate; i <= candidates.size() - needed; i++) {
            Apply apply = candidates.get(i);
            String occupancyKey = apply.getUser().getId() + "-" + apply.getDetailWorktime().getDate();
            
            if (userOccupancy.contains(occupancyKey)) continue;

            userOccupancy.add(occupancyKey);
            currentFixed.add(apply);
            
            fillSlot(slotIndex, needed - 1, i + 1, candidates, currentFixed, userOccupancy);
            
            currentFixed.remove(currentFixed.size() - 1);
            userOccupancy.remove(occupancyKey);
            
            if (remainingLimit == 0) return;
        }
    }

    private Map<DayOfWeek, SortedMap<Worktime, List<Apply>>> generateDayOfWeekSortedMap(List<Apply> applies) {
        Map<DayOfWeek, SortedMap<Worktime, List<Apply>>> recommend = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            SortedMap<Worktime, List<Apply>> appliesByWorktime = new TreeMap<>(
                    Comparator.comparing(Worktime::getStartTime));
            List<Apply> appliesByDay = filterApplies(applies, day, null);

            for (Worktime worktime : this.worktimes) {
                List<Apply> appliesByDayAndWorktime = filterApplies(appliesByDay, day, worktime);
                if (!appliesByDayAndWorktime.isEmpty()) {
                    Worktime key = appliesByDayAndWorktime.get(0).getDetailWorktime().getWorktime();
                    appliesByWorktime.put(key, appliesByDayAndWorktime);
                }
            }
            recommend.put(day, appliesByWorktime);
        }
        return recommend;
    }

    private List<Apply> filterApplies(List<Apply> applies, DayOfWeek day, Worktime worktime) {
        return applies.stream()
                .filter(apply -> apply.getDetailWorktime().getDayOfWeek().equals(day) &&
                        (worktime == null || apply.getDetailWorktime().getWorktime().getId().equals(worktime.getId())))
                .collect(Collectors.toList());
    }
}
