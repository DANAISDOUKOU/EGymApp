package dipl.danai.app.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
@Repository
public interface GymRepository extends JpaRepository<Gym, Long>{
	@Query(value="SELECT g FROM Gym g WHERE gym_name=?1")
	public Gym findByName(String name);
	@Query(value="SELECT g FROM Gym g WHERE email=?1")
	public Gym findByEmail(String email);
	@Query(value="SELECT g.gymMembers FROM Gym g WHERE g.gym_id=:gymId ")
	public Set<Athletes> findMembers(@Param("gymId") Long gymId);
	@Query(value = "SELECT * FROM gyms WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon", nativeQuery = true)
	public List<Gym> findByLatitudeBetweenAndLongitudeBetween( @Param("minLat") double minLat,@Param("maxLat") double maxLat, @Param("minLon") double minLon, @Param("maxLon") double maxLon);


}
