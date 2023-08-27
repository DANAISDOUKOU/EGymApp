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
import org.springframework.web.bind.annotation.RequestParam;

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
		
	@GetMapping("/gym/createMembershipType")
	public String createMembershipType(Model model,Authentication authentication) {
		MembershipType membership=new MembershipType();
		Gym gym = gymService.getGymByEmail(authentication.getName());
		model.addAttribute("gymId", gym.getGym_id());
		model.addAttribute("membership",membership);
		return "gym/createMembershipType";
	}
	
	@PostMapping("/gym/createMembershipType")
	public String createMembershipTypeP(Model model,@Valid @ModelAttribute("membership") MembershipType membership,BindingResult bindingResult,@RequestParam(value="gymId") Long gymId) throws SQLException {
		membership.setGym(gymService.getGymById(gymId));
		membershipTypeService.saveMembership(membership);
		model.addAttribute("successMessage", "MembershipType registered successfully!");
		return "gym/createMembershipType";
	}
	
}
