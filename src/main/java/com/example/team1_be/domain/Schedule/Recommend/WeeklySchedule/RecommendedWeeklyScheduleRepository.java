package com.example.team1_be.domain.Schedule.Recommend.WeeklySchedule;

import java.util.List;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendedWeeklyScheduleRepository extends JpaRepository<RecommendedWeeklySchedule, Long> {
	@Query("select r " +
		"from RecommendedWeeklySchedule r " +
		"join fetch r.recommendedWorktimeApplies " +
		"where r.week.id = :weekId")
	List<RecommendedWeeklySchedule> findByWeek(@Param("weekId") Long weekId);
}
