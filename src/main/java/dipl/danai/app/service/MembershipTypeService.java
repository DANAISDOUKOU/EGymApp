package dipl.danai.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.MembershipType;
import dipl.danai.app.repository.MembershipTypeRepository;

@Service
public class MembershipTypeService  {
	@Autowired
	MembershipTypeRepository membershipRepository;
	

	public void saveMembership(MembershipType membershipType) {
		membershipRepository.save(membershipType);	
	}
	
	public List<MembershipType> findAll() {
		List<MembershipType> memberships=membershipRepository.findAll();
		return memberships;
	}
	

}
