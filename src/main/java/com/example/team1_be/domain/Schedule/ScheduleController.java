package com.example.team1_be.domain.Schedule;

import java.time.LocalDate;
import java.time.YearMonth;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.team1_be.domain.Schedule.DTO.FixSchedule;
import com.example.team1_be.domain.Schedule.DTO.GetApplies;
import com.example.team1_be.domain.Schedule.DTO.GetDailyFixedApplies;
import com.example.team1_be.domain.Schedule.DTO.GetFixedWeeklySchedule;
import com.example.team1_be.domain.Schedule.DTO.GetWeekStatus;
import com.example.team1_be.domain.Schedule.DTO.LoadLatestSchedule;
import com.example.team1_be.domain.Schedule.DTO.PostApplies;
import com.example.team1_be.domain.Schedule.DTO.RecommendSchedule;
import com.example.team1_be.domain.Schedule.DTO.RecruitSchedule;
import com.example.team1_be.domain.Schedule.DTO.WeeklyScheduleCheck;
import com.example.team1_be.domain.Schedule.Service.ScheduleService;
import com.example.team1_be.utils.ApiUtils;
import com.example.team1_be.utils.security.auth.UserDetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
	private final ScheduleService scheduleService;

	@PostMapping("/worktime")
	public ResponseEntity<ApiUtils.ApiResult<String>> recruitSchedule(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid RecruitSchedule.Request request) {

		scheduleService.recruitSchedule(userDetails.getUser(), request);
		ApiUtils.ApiResult<String> result = ApiUtils.success(null);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/worktime")
	public ResponseEntity<ApiUtils.ApiResult<LoadLatestSchedule.Response>> loadLatestSchedule(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("startWeekDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startWeekDate) {

		LoadLatestSchedule.Response responseDTO = scheduleService.loadLatestSchedule(userDetails.getUser(),
			startWeekDate);
		ApiUtils.ApiResult<LoadLatestSchedule.Response> result = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/remain/week")
	public ResponseEntity<ApiUtils.ApiResult<WeeklyScheduleCheck.Response>> weeklyScheduleCheck(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("startWeekDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startWeekDate) {

		WeeklyScheduleCheck.Response responseDTO = scheduleService.weeklyScheduleCheck(userDetails.getUser(),
			startWeekDate);
		ApiUtils.ApiResult<WeeklyScheduleCheck.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/recommend")
	public ResponseEntity<ApiUtils.ApiResult<RecommendSchedule.Response>> recommendSchedule(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("startWeekDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

		RecommendSchedule.Response responseDTO = scheduleService.recommendSchedule(userDetails.getUser(), date);
		ApiUtils.ApiResult<RecommendSchedule.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/fix")
	public ResponseEntity<ApiUtils.ApiResult<String>> fixSchedule(
		@AuthenticationPrincipal CustomUserDetails userDetail,
		@RequestBody FixSchedule.Request request) {

		scheduleService.fixSchedule(userDetail.getUser(), request);
		ApiUtils.ApiResult<String> response = ApiUtils.success(null);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/fix/month")
	public ResponseEntity<ApiUtils.ApiResult<GetFixedWeeklySchedule.Response>> getFixedWeeklySchedule(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth requestMonth,
		@RequestParam(value = "userId", required = false) Long userId) {

		GetFixedWeeklySchedule.Response responseDTO;

		if (null == userId) {
			responseDTO = scheduleService.getPersonalWeeklyFixedSchedule(userDetails.getUser(),
					requestMonth);
		} else {
			responseDTO = scheduleService.getFixedWeeklySchedule(userDetails.getUser(),
					requestMonth, userId);
		}
		ApiUtils.ApiResult<GetFixedWeeklySchedule.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/fix/day")
	public ResponseEntity<ApiUtils.ApiResult<GetDailyFixedApplies.Response>> getDailyFixedApplies(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("selectedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate) {

		GetDailyFixedApplies.Response responseDTO = scheduleService.getDailyFixedApplies(userDetails.getUser(),
			selectedDate);
		ApiUtils.ApiResult<GetDailyFixedApplies.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/status")
	public ResponseEntity<ApiUtils.ApiResult<GetWeekStatus.Response>> getWeekStatus(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("startWeekDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startWeekDate) {

		GetWeekStatus.Response responseDTO = scheduleService.getWeekStatus(userDetails.getUser(), startWeekDate);
		ApiUtils.ApiResult<GetWeekStatus.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/application")
	public ResponseEntity<ApiUtils.ApiResult<GetApplies.Response>> getApplies(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam("startWeekDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startWeekDate) {

		GetApplies.Response responseDTO = scheduleService.getApplies(userDetails.getUser(), startWeekDate);
		ApiUtils.ApiResult<GetApplies.Response> response = ApiUtils.success(responseDTO);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/application")
	public ResponseEntity<ApiUtils.ApiResult<String>> postApplies(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody PostApplies.Request requestDTO) {
		
		scheduleService.postApplies(userDetails.getUser(), requestDTO);
		ApiUtils.ApiResult<String> response = ApiUtils.success(null);
		return ResponseEntity.ok(response);
	}
}
