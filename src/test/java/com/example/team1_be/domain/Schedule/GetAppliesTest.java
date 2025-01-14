package com.example.team1_be.domain.Schedule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.team1_be.domain.Schedule.DTO.PostApplies;
import com.example.team1_be.domain.Worktime.Worktime;
import com.example.team1_be.util.WithMockCustomMemberUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("/data.sql")
public class GetAppliesTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;

	private final String STARTWEEKDATE = "2023-11-13";

	@DisplayName("스케줄 신청/수정 조회 요청 성공")
	@WithMockCustomMemberUser(userId = "2")
	@Test
	void shouldRetrieveScheduleApplicationSuccessfully() throws Exception {
		LocalDate startWeekDate = LocalDate.parse(STARTWEEKDATE);
		String URL = String.format("/api/schedule/application?startWeekDate=%s", startWeekDate);
		ResultActions perform = mvc.perform(get(URL));
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("스케줄 신청/수정 제출 DTO 확인")
	@WithMockCustomMemberUser(userId = "2")
	@Test
	void shouldCheckScheduleApplicationDto() throws Exception {
		LocalDate weekStartDate = LocalDate.parse(STARTWEEKDATE);

		List<SortedMap<Worktime, Boolean>> weeklyApplies = new ArrayList<>();
		List<Worktime> worktimes = new ArrayList<>();
		worktimes.add(Worktime.builder().id(4L).startTime(LocalTime.now()).build());
		worktimes.add(Worktime.builder().id(5L).startTime(LocalTime.now().plusHours(1)).build());
		worktimes.add(Worktime.builder().id(6L).startTime(LocalTime.now().plusHours(2)).build());

		for (DayOfWeek day : DayOfWeek.values()) {
			SortedMap<Worktime, Boolean> dailyApplies = new TreeMap<>(
				Comparator.comparing(Worktime::getStartTime));
			for (Worktime worktime : worktimes) {
				dailyApplies.put(worktime, day.ordinal() % 2 == 1);
			}
			weeklyApplies.add(dailyApplies);
		}
		PostApplies.Request requestDTO = new PostApplies.Request(weekStartDate, weeklyApplies);
		String DTO = om.writeValueAsString(requestDTO);
		System.out.println(DTO);
	}

	@DisplayName("스케줄 신청/수정 제출 DTO 포함 요청")
	@WithMockCustomMemberUser(userId = "7")
	@Test
	void shouldSubmitScheduleApplicationWithDto() throws Exception {
		// given
		LocalDate weekStartDate = LocalDate.parse(STARTWEEKDATE);

		List<SortedMap<Worktime, Boolean>> weeklyApplies = new ArrayList<>();
		List<Worktime> worktimes = new ArrayList<>();
		worktimes.add(Worktime.builder().id(4L).startTime(LocalTime.now()).build());
		worktimes.add(Worktime.builder().id(5L).startTime(LocalTime.now().plusHours(1)).build());
		worktimes.add(Worktime.builder().id(6L).startTime(LocalTime.now().plusHours(2)).build());

		for (DayOfWeek day : DayOfWeek.values()) {
			SortedMap<Worktime, Boolean> dailyApplies = new TreeMap<>(
				Comparator.comparing(Worktime::getStartTime));
			for (Worktime worktime : worktimes) {
				dailyApplies.put(worktime, day.ordinal() % 2 == 1);
			}
			weeklyApplies.add(dailyApplies);
		}
		PostApplies.Request requestDTO = new PostApplies.Request(weekStartDate, weeklyApplies);
		String DTO = om.writeValueAsString(requestDTO);
		System.out.println(DTO);

		// when
		ResultActions perform = mvc.perform(
			put("/api/schedule/application").contentType(MediaType.APPLICATION_JSON).content(DTO));
		perform.andExpect(status().isOk());
		perform.andDo(print());
		System.out.println(DTO);
	}
}
