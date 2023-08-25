package dipl.danai.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;


@Repository
public interface AthleteRepository extends JpaRepository<Athletes,Long> {
	@Query(value="SELECT a FROM Athletes a WHERE email=?1")
	public Athletes findByEmail(String email);
	@Query("SELECT c FROM ClassOfSchedule c JOIN c.participants p WHERE p = :athlete")
    public List<ClassOfSchedule> findAttendedClassesByParticipantsContaining(Athletes athlete);

}
