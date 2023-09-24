package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.MembershipType;

@Repository
public interface MembershipTypeRepository extends JpaRepository<MembershipType,Long> {

}
