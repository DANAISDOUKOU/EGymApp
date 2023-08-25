package dipl.danai.app.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.InstructorRepository;

@Controller
public class InstructorController {
	@Autowired
	InstructorRepository instructorRepository;
	@Autowired
	AthleteRepository athleteRepository;
	@Autowired
	ClassOfScheduleRepository classScheduleRepository;
	
	@GetMapping(value = {"/instructor/dashboard"})
    public String instructorPage(){
        return "instructor/dashboard";
    }
	
	@GetMapping(value={"instructor/gymsIWork"})
	public String getGymsThatIWork(Authentication authentication, Model model) {
		Instructor instructor=instructorRepository.findByEmail(authentication.getName());
		Set<Gym> gyms=instructor.getGyms();
		model.addAttribute("gyms", gyms);
		return "instructor/gymsIWork";
	}
	
}
