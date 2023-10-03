package dipl.danai.app.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long>{
	 @Query("SELECT s FROM Schedule s " +
	           "INNER JOIN s.gyms gs " +
	           "WHERE gs.id = :gymId AND s.work_out_date = :date")
	   Schedule findByGymIdAndDate(@Param("gymId") Long gymId, @Param("date") Date date);
}
