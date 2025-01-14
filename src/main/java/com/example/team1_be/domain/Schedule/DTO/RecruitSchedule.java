package com.example.team1_be.domain.Schedule.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.example.team1_be.domain.Worktime.Worktime;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecruitSchedule {
	@Getter
	@NoArgsConstructor
	public static class Request {
		@NotNull(message = "스케줄의 시작날짜를 입력하세요.")
		private LocalDate weekStartDate;

		@Valid
		@NotEmpty(message = "근무일정이 비어있습니다.")
		private List<Template> template;
		/**
		 * "{"weekStartDate":"2023-11-27","amount":[[2],[0],[0],[0],[0],[0],[0]],"template":[{"title":"오픈","startTime":"10:00:00","endTime":"12:00:00"}]}"
		 */

		@NotEmpty(message = "지원자가 비어있을 수 없습니다.")
		private List<List<Long>> amount;

		public List<Worktime> getWorktimes() {
			return this.template.stream()
				.map(Template::getWorktime)
				.collect(Collectors.toList());
		}

		@Getter
		@NoArgsConstructor
		private static class Template {
			private String title;
			private LocalTime startTime;
			private LocalTime endTime;

			public Template(Worktime worktime) {
				this.title = worktime.getTitle();
				this.startTime = worktime.getStartTime();
				this.endTime = worktime.getEndTime();
			}

			public Worktime getWorktime() {
				return Worktime.builder()
					.title(this.title)
					.startTime(this.startTime)
					.endTime(this.endTime)
					.build();
			}
		}
	}
}
