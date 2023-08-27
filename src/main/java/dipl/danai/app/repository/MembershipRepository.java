package dipl.danai.app.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;

@Repository
public interface MembershipRepository extends JpaRepository<Membership,Long>{
	 	@Query("SELECT m FROM Membership m " +
	           "WHERE m.membershipType.membership_type_id = :membershipTypeId " +
	           "AND m.athlete.athlete_id = :athleteId")
	    Membership findMembership(Long membershipTypeId, Long athleteId);
	 	
	 	 @Query("SELECT m FROM Membership m " +
	 	           "JOIN m.membershipType mt " +
	 	           "JOIN mt.gym g " +
	 	           "WHERE m.athlete = :athlete AND g.gym_id = :gymId")
	 	 Membership findByAthleteAndMembershipType_Gym(Long gymId, Athletes athlete);
	 	
}
