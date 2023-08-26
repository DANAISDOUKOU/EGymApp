package dipl.danai.app.controller;

import java.sql.SQLException;
import org.springframework.security.core.Authentication;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.MembershipTypeService;

@Controller
public class MembershipController {
	
	@Autowired
	MembershipTypeService membershipTypeService;
	
	@Autowired
	GymService gymService;
	
	@GetMapping("/gym/addMembershipType")
	public String addMembershipType(Authentication authentication,Model model) {
		Gym gym = gymService.getGymByEmail(authentication.getName());
		model.addAttribute("memberships", membershipTypeService.findAll());
		model.addAttribute("selectedMembership", new MembershipType());
		model.addAttribute("gym", gym);
		return "gym/addMembershipType";
	}
	
	@PostMapping("/gym/addMembershipType")
	public String addMembershipType(Authentication authentication,@ModelAttribute("selectedMembership") MembershipType selectedMembership, Model model) {
		MembershipType membershipType = membershipTypeService.findMembershiById(selectedMembership.getMembership_type_id());
		Gym gym = gymService.getGymByEmail(authentication.getName());
		if (membershipType != null) {
	        selectedMembership.setMembership_amount(membershipType.getMembership_amount());
	        selectedMembership.setMembership_period(membershipType.getMembership_period());
	    }
	    model.addAttribute("gym",gym);
	    model.addAttribute("memberships", membershipTypeService.findAll());
	    model.addAttribute("selectedMembership", selectedMembership);
	    gymService.addMembershipToGym(gym.getGym_id(), selectedMembership);
	    return "gym/addMembershipType";
	}
	
	@GetMapping("/gym/createMembershipType")
	public String createMembershipType(Model model) {
		MembershipType membership=new MembershipType();
		model.addAttribute("membership",membership);
		return "gym/createMembershipType";
	}
	
	@PostMapping("/gym/createMembershipType")
	public String createMembershipTypeP(Model model,@Valid @ModelAttribute("membership") MembershipType membership,BindingResult bindingResult) throws SQLException {
		membershipTypeService.saveMembership(membership);
		model.addAttribute("successMessage", "MembershipType registered successfully!");
		return "gym/createMembershipType";
	}
	
	@GetMapping("/gym/deleteMembershipType")
	public String deleteMembershipType() {
		
		return "gym/deleteMembershipType";
	}
}
