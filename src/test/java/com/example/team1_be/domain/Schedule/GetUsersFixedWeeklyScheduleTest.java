package com.example.team1_be.domain.Schedule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

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
public class GetUsersFixedWeeklyScheduleTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;

	private final String CURRENTYEARMONTH = "2023-11";
	@DisplayName("개인 확정 스케줄 조회 성공")
	@WithMockCustomAdminUser(userId = "2")
	@Test
	void shouldRetrieveUsersFixedWeeklyScheduleSuccessfully() throws Exception {
		YearMonth month = YearMonth.parse(CURRENTYEARMONTH);
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/fix/month?month=%s", month)));
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("개인 확정 스케줄 조회 실패(파라미터 에러)")
	@WithMockCustomAdminUser(userId = "2")
	@Test
	void shouldFailToRetrieveUsersFixedWeeklyScheduleDueToParameterError() throws Exception {
		ResultActions perform = mvc.perform(
			get(String.format("/api/schedule/fix/month?month=%s", "2023")));
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}
}
