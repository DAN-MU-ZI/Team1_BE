package com.example.team1_be.domain.Apply;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.team1_be.domain.User.User;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
	@Query("select a " +
		"from Apply a " +
		"where a.detailWorktime.worktime.id in (:detailWorktimeIds) "
			+ "and a.detailWorktime.amount != 0")
	List<Apply> findAppliesByWorktimeIds(@Param("detailWorktimeIds") List<Long> detailWorktimeIds);

	@Query("select a "
		+ "from Apply a "
		+ "where a.user.id = :userId "
		+ "and a.detailWorktime.date = :date "
		+ "and a.status = :status")
	List<Apply> findByUserAndDateAndStatus(@Param("userId") Long userId, @Param("date") LocalDate date,
		@Param("status") ApplyStatus status);

	@Query("select a.user "
		+ "from Apply a "
		+ "where a.detailWorktime.id = :detailWorktimeId "
		+ "and a.status = :status ")
	List<User> findUsersByWorktimeAndApplyStatus(@Param("detailWorktimeId") Long id,
		@Param("status") ApplyStatus status);

	@Query("select a "
		+ "from Apply a "
		+ "where a.user.id = :userId "
		+ "and a.detailWorktime.worktime.id = :worktimeId "
		+ "and a.detailWorktime.dayOfWeek = :dayOfWeek")
	Optional<Apply> findByUserAndWorktimeAndDay(@Param("userId") Long userId,
		@Param("worktimeId") Long worktimeId,
		@Param("dayOfWeek") DayOfWeek day);

	@Query("select a "
		+ "from Apply a "
		+ "where a.user.id = :userId "
		+ "and a.detailWorktime.id in (:detailWorktimeIds)")
	List<Apply> findByUserAndDetailWorktimeIds(@Param("userId") Long userId,
		@Param("detailWorktimeIds") List<Long> worktimeids);

	@Query("select a "
			+ "from Apply a "
			+ "where a.user.id = :userId "
			+ "and a.detailWorktime.worktime.week.id = :weekId "
			+ "and a.status = :applyStatus")
    List<Apply> findByUserAndWeekAndStatus(@Param("userId")Long userId, @Param("weekId")Long weekId, @Param("applyStatus")ApplyStatus fix);

	@Query("select a "
			+ "from Apply a "
			+ "where a.detailWorktime.worktime.week.id = :weekId "
			+ "and a.status = :applyStatus")
	List<Apply> findByWeekAndStatus(@Param("weekId")Long weekId, @Param("applyStatus")ApplyStatus fix);
}
