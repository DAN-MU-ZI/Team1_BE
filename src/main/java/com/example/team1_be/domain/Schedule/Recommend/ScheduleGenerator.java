package com.example.team1_be.domain.Schedule.Recommend;

import com.example.team1_be.domain.DetailWorktime.DetailWorktime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.example.team1_be.domain.Apply.Apply;
import com.example.team1_be.domain.Worktime.Worktime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleGenerator {
	private final int GEN_LIMIT = 3;
	private final List<Worktime> worktimes;
	private final List<Apply> applyList;
	private final Map<Long, Long> requestMap;
	private final List<List<Apply>> generatedApplies;
	private int limit;
	private int index;
	private TreeMap<Long, Long> remainRequestMap;
	private List<Apply> fixedApplies;

	public ScheduleGenerator(List<Worktime> worktimes, List<Apply> applyList, Map<Long, Long> requestMap) {
		this.worktimes = worktimes;
		this.applyList = applyList;
		this.requestMap = requestMap.entrySet()
				.stream()
				.filter(a -> a.getValue() != 0)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		this.generatedApplies = new ArrayList<>();
		this.limit = GEN_LIMIT;
		this.index = 0;
		this.remainRequestMap = new TreeMap<>(this.requestMap);
		this.fixedApplies = new ArrayList<>();
	}

	public List<Map<DayOfWeek, SortedMap<Worktime, List<Apply>>>> generateSchedule() {
		log.info("스케줄을 생성합니다.");
		recursiveSearch(applyList, 0, requestMap, new ArrayList<>());

		List<Map<DayOfWeek, SortedMap<Worktime, List<Apply>>>> result = this.generatedApplies.stream()
			.map(this::generateDayOfWeekSortedMap)
			.collect(Collectors.toList());

		log.info("스케줄 생성이 완료되었습니다.");
		return result;
	}

	private void recursiveSearch(List<Apply> applyList, int index, Map<Long, Long> remainRequestMap,
								 List<Apply> fixedApplies) {
		if (isSearchComplete(remainRequestMap)) {
			log.info("완성된 스케줄 존재");
			if (limit != 0) {
				log.info("완성된 스케줄 추가");
				this.generatedApplies.add(new ArrayList<>(fixedApplies));
				limit--;
			}
			return;
		}

		while (index < applyList.size()) {
			// 신청가능한지 미리 파악하기
			// 신청가능한 인원이 있는지 확인
			// 남은 신청 가져오기
			List<Apply> temp = new ArrayList<>();
			for (int i=0;i<applyList.size();i++) {
				if(i>=index){
					temp.add(applyList.get(i));
				}
			}
			Map<Long, Integer> collectmap = remainRequestMap.entrySet()
					.stream()
					.filter(a -> a.getValue() != 0)
					.collect(Collectors.toMap(Entry::getKey, longLongEntry -> 0));
			
			for(Long remainId:collectmap.keySet()){
				Optional<Apply> optionalApply = temp.stream()
						.filter(x -> Objects.equals(x.getDetailWorktime().getId(), remainId)).findFirst();
				if (optionalApply.isEmpty()) {
					log.info("필요한 apply를 찾지 못함");
					return;
				}
			}
			log.info("{} {}", index, applyList.size());
			Map<Long, Long> copiedRequestMap = new HashMap<>(remainRequestMap);
			List<Apply> copiedFixedApplies = new ArrayList<>(fixedApplies);

			Apply selectedApply = applyList.get(index);
			Long selectedWorktimeId = selectedApply.getDetailWorktime().getId();
			Long selectedAppliers = copiedRequestMap.get(selectedWorktimeId);

			if (selectedAppliers != null && selectedAppliers != 0) {
				copiedRequestMap.put(selectedWorktimeId, selectedAppliers - 1);
				copiedFixedApplies.add(selectedApply);
				recursiveSearch(applyList, index + 1, copiedRequestMap, copiedFixedApplies);
				if (limit == 0) {
					return;
				}
			}

			index++;
		}
	}

	private boolean isSearchComplete(Map<Long, Long> remainRequestMap) {
		return remainRequestMap.values().stream().mapToInt(Long::intValue).sum() == 0;
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
