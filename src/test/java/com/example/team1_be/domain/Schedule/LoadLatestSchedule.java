package com.example.team1_be.domain.Schedule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.team1_be.util.WithMockCustomAdminUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("/data.sql")
public class LoadLatestSchedule {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;

	private final String BEFOREWEEK = "2023-11-13";
	private final String STRATWEEKDATE = "2023-11-20";

	@DisplayName("최근 스케줄 조회 성공")
	@WithMockCustomAdminUser(userId = "2")
	@Test
	void shouldRetrieveRecentScheduleSuccessfully() throws Exception {
		LocalDate startWeekDate = LocalDate.parse(STRATWEEKDATE);
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/worktime?startWeekDate=%s", startWeekDate)));
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("최근 스케줄 조회 실패시 빈배열 제공")
	@WithMockCustomAdminUser(userId = "2")
	@Sql("emptyRecentSchedule.sql")
	@Test
	void shouldRetrieveEmptyRecentScheduleSuccessfully() throws Exception {
		LocalDate startWeekDate = LocalDate.parse(BEFOREWEEK);
		ResultActions perform = mvc.perform(
				get(String.format("/api/schedule/worktime?startWeekDate=%s", startWeekDate)));
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}
}
