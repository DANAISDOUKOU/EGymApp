package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Schedule;
@Repository
public interface ClassOfScheduleRepository extends JpaRepository<ClassOfSchedule,Long>{
	
	@Query("SELECT s FROM Schedule s JOIN s.ScheduleClasses c WHERE c.classOfScheduleId = :classOfScheduleId")
    Schedule findScheduleByClassOfScheduleId(@Param("classOfScheduleId") Long classOfScheduleId);
}
