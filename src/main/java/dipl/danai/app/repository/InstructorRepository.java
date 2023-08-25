package dipl.danai.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Instructor;


@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

	@Query(value="SELECT i FROM Instructor i WHERE instructor_name=?1")
	public Instructor findByName(String name);
	@Query(value="SELECT i FROM Instructor i WHERE email=?1")
	public Instructor findByEmail(String email);
	@Query("SELECT c FROM ClassOfSchedule c JOIN c.instructor i JOIN c.room r JOIN r.gym g " +
	        "WHERE i.instructor_id = :instructorId AND g.gym_id = :gymId")
	 List<ClassOfSchedule> findClassesByInstructorAndGym(Long instructorId, Long gymId);
}
