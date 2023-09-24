package dipl.danai.app.controller;

import java.util.Base64;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.service.InstructorService;

@Controller
public class InstructorController {
	@Autowired
	InstructorService instructorService;


	@GetMapping(value = {"/instructor/dashboard"})
    public String instructorPage(Authentication authentication, Model model){
		Instructor instructor=instructorService.getByEmail(authentication.getName());
		Set<Gym> gyms=instructor.getGyms();
		for (Gym gym : gyms) {
 			byte[] pictureBytes = gym.getPicture(); 
 			String pictureBase64 = Base64.getEncoder().encodeToString(pictureBytes);
 			gym.setProfilePictureBase64(pictureBase64); 
 		}
		model.addAttribute("gyms", gyms);
        return "instructor/dashboard";
    }

	@GetMapping(value={"instructor/gymsIWork"})
	public String getGymsThatIWork(Authentication authentication, Model model) {
		Instructor instructor=instructorService.getByEmail(authentication.getName());
		Set<Gym> gyms=instructor.getGyms();
		model.addAttribute("gyms", gyms);
		return "instructor/gymsIWork";
	}

}
