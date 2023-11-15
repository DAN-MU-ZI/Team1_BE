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
public class GetWeekStatus {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;

	private final String BEFOREWEEK = "2023-11-13";
	private final String STRATWEEKDATE = "2023-11-20";
	private final String NEXTWEEKDATE = "2023-11-27";

	@DisplayName("주별 상태 조회 closed")
	@WithMockCustomAdminUser
	@Test
	void shouldRetrieveClosedWeeklyStatus() throws Exception {
		// given
		LocalDate startDate = LocalDate.parse(BEFOREWEEK);

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/status?startWeekDate=%s", startDate)));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("주별 상태 조회 inProgress")
	@WithMockCustomAdminUser
	@Test
	void shouldRetrieveInProgressWeeklyStatus() throws Exception {
		// given
		LocalDate startDate = LocalDate.parse(STRATWEEKDATE);

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/status?startWeekDate=%s", startDate)));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("주별 상태 조회 allocatable")
	@WithMockCustomAdminUser
	@Test
	void shouldRetrieveAllocatableWeeklyStatus() throws Exception {
		// given
		LocalDate startDate = LocalDate.parse(NEXTWEEKDATE);

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/status?startWeekDate=%s", startDate)));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}
}
