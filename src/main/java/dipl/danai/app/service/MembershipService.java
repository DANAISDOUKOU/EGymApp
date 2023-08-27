package dipl.danai.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.repository.MembershipRepository;
import dipl.danai.app.repository.MembershipTypeRepository;

@Service
public class MembershipService  {
	@Autowired
	MembershipTypeRepository membershipTypeRepository;
	

	@Autowired
	MembershipRepository membershiRepository;
	
	public void saveMembershipType(MembershipType membershipType) {
		membershipTypeRepository.save(membershipType);	
	}
	
	public List<MembershipType> findAll() {
		List<MembershipType> memberships=membershipTypeRepository.findAll();
		return memberships;
	}
	
	public MembershipType findMembershiById(Long id) {
		MembershipType membershipType=membershipTypeRepository.findById(id).orElse(null);
		return membershipType;
	}

	public void saveMembership(Membership membership) {
		membershiRepository.save(membership);
		
	}
	
	public Membership findMembership(Long membershipTypeId,Long athleteId) {
		return membershiRepository.findMembership(membershipTypeId,athleteId);
	}
		

}
