package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.ClassOccurrence;

@Repository
public interface ClassOccurrenceRepository extends JpaRepository<ClassOccurrence, Long> {
	@Query("SELECT co FROM ClassOccurrence co WHERE co.schedule.schedule_id = :scheduleId AND co.classOfSchedule.classOfScheduleId = :classOfScheduleId")
	ClassOccurrence findByScheduleIdAndClassOfScheduleId(Long scheduleId, Long classOfScheduleId);

}
