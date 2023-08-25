package dipl.danai.app.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.ClassRating;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.ClassRatingRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;

@Controller
public class AthelteController {
	
	@Autowired
	private AthleteRepository athleteRepository;
	
	@Autowired
	private GymRepository gymRepository;
	
	@Autowired 
	private ClassOfScheduleRepository classOfScheduleRepository;
	
	@Autowired
	private ClassRatingRepository classRatingRepo;
	
	@Autowired
	private InstructorRepository instructorRepository;
	
    @GetMapping(value = {"/athlete/dashboard"})
    public String athletePage(){
        return "athlete/dashboard";
    }
	
	
	@GetMapping(value={"/athlete/seeAllGyms"})
	public String seeAllGyms(Authentication authentication,Model model) {
		List<Gym> gyms=gymRepository.findAll();
		model.addAttribute("gyms",gyms);
		return "athlete/seeAllGyms";
	}

	@GetMapping(value={"/athlete/GymsThatIAmMember"})
	public String seeGymsThatIamMember(Authentication authentication,Model model) {
		 String athleteEmail = authentication.getName();
		 Athletes athlete = athleteRepository.findByEmail(athleteEmail);
		 Set<Gym> gyms = athlete.getGyms();
		 model.addAttribute("gyms", gyms);
		 return "athlete/gymsThatIAmMember";
	}
	
	@GetMapping("/athlete/history")
	public String viewAttendanceHistory(Model model, Authentication authentication, @RequestParam(name = "gymId") Long gymId) {
	    Athletes athlete = athleteRepository.findByEmail(authentication.getName());
	    Gym gym = gymRepository.findById(gymId).orElse(null);
	    List<ClassOfSchedule> attendedClasses = athleteRepository.findAttendedClassesByParticipantsContaining(athlete);    
	    List<ClassOfSchedule> attendedClassesInGym = attendedClasses.stream()
	            .filter(classOfSchedule -> classOfSchedule.getSchedules().stream()
	                    .anyMatch(schedule -> schedule.getGyms().contains(gym)))
	            .collect(Collectors.toList());
	    model.addAttribute("gym",gym);
	    model.addAttribute("attendedClasses", attendedClassesInGym);
	    return "athlete/history";
	}
	
	@PostMapping("/rate-class")
	public String rateClass(@RequestParam Long classOfScheduleId, @RequestParam Long gymId,
	                        @RequestParam double instructorRating, @RequestParam double accuracyRating,
	                        @RequestParam double crowdingRating, Authentication authentication) {
	    Athletes athlete = athleteRepository.findByEmail(authentication.getName());
	    Gym gym = gymRepository.findById(gymId).orElse(null);
	    ClassOfSchedule classOfSchedule = classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
	    Instructor instructor=classOfSchedule.getInstructor();
	    ClassRating classRating = new ClassRating();
	    classRating.setGym(gym);
	    classRating.setAthlete(athlete);
	    classRating.setClassOfSchedule(classOfSchedule);
	    classRating.setInstructorRating(instructorRating);
	    classRating.setAccuracyRating(accuracyRating);
	    classRating.setCrowdingRating(crowdingRating);
	    classRating.setInstructor(instructor);
	    classRatingRepo.save(classRating);
	    
	    return "redirect:/athlete/history";
	}


}
