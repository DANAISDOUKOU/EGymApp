package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.AthleteClassScheduleReservation;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;

@Repository
public interface WeeksReservedRepository extends JpaRepository<AthleteClassScheduleReservation,Long> {
	 @Query("SELECT acsr.reservedWeek FROM AthleteClassScheduleReservation acsr " +
	           "WHERE acsr.classOfSchedule = :classOfSchedule " +
	           "AND acsr.athlete = :athlete")
	    Integer findReservedWeekByClassOfScheduleAndAthlete(
	            @Param("classOfSchedule") ClassOfSchedule classOfSchedule,
	            @Param("athlete") Athletes athlete
	    );

	 @Query("SELECT acsr FROM AthleteClassScheduleReservation acsr " +
	           "WHERE acsr.athlete = :athlete " +
	           "AND acsr.classOfSchedule = :classOfSchedule")
	    AthleteClassScheduleReservation findByAthleteAndClass(
	        @Param("athlete") Athletes athlete,
	        @Param("classOfSchedule") ClassOfSchedule classOfSchedule
	    );
}
