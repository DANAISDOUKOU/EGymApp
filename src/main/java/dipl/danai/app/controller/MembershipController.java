package dipl.danai.app.controller;

import java.sql.SQLException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import dipl.danai.app.service.MembershipService;

@Controller
public class MembershipController {

	@Autowired
	MembershipService membershipTypeService;

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
	public String createMembershipTypeP(
	    Model model,
	    @Valid @ModelAttribute("membership") MembershipType membershipType,
	    BindingResult bindingResult,
	    @RequestParam(value = "gymId") Long gymId
	) throws SQLException {
	    Gym gym = gymService.getGymById(gymId);

	    if ("lessons".equalsIgnoreCase(membershipType.getMembership_type_name())) {
	        try {
	            int lessons = Integer.parseInt(membershipType.getMembership_period());
	            membershipType.setRemainingLessons(lessons);
	        } catch (NumberFormatException e) {
	            // Handle invalid input if necessary
	            bindingResult.rejectValue("membership_period", "error.invalid", "Invalid number of lessons");
	            return "gym/createMembershipType";
	        }
	    }

	    membershipType.setGym(gym);
	    membershipTypeService.saveMembershipType(membershipType);
	    model.addAttribute("successMessage", "MembershipType registered successfully!");
	    return "gym/createMembershipType";
	}

}
