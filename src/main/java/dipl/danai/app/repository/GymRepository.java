package dipl.danai.app.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.MembershipType;
@Repository
public interface GymRepository extends JpaRepository<Gym, Long>{
	@Query(value="SELECT g FROM Gym g WHERE gym_name=?1")
	public Gym findByName(String name);
	@Query(value="SELECT g FROM Gym g WHERE email=?1")
	public Gym findByEmail(String email);
	@Query(value="SELECT g.gymMembers FROM Gym g WHERE g.gym_id=:gymId ")
	public Set<Athletes> findMembers(@Param("gymId") Long gymId);
	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM Gym g JOIN g.gym_memberships m WHERE m.membership_type_id = :id")
	public boolean existsByMembershipTypeId(Long id);
	 @Query("SELECT m FROM Gym g " +
	           "JOIN g.gym_memberships gm " +
	           "JOIN gm.athletes ath " +
	           "JOIN ath.memberships m " +
	           "WHERE g.gym_id =?1 " +
	           "AND ath.athlete_id = ?2")
	public MembershipType findExistingMebership(Long gymId,Long athleteId);
	
}
