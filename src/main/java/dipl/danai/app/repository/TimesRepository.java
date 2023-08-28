package dipl.danai.app.repository;

import java.sql.Time;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dipl.danai.app.model.Times;

public interface TimesRepository extends JpaRepository<Times, Long>{
	@Query(value="SELECT t FROM Times t WHERE time_start=?1")
	public Times findByStartTime(Time time);
}
