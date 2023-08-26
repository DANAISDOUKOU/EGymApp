package dipl.danai.app.controller;

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
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.ClassRatingService;
import dipl.danai.app.service.GymService;

@Controller
public class AthelteController {
	
	@Autowired
	private AthleteService athleteService;
	
	@Autowired
	private GymService gymService;
	
	@Autowired 
	private ClassOfScheduleService classOfScheduleService;
	
	@Autowired
	private ClassRatingService classRatingService;
		
    @GetMapping(value = {"/athlete/dashboard"})
    public String athletePage(){
        return "athlete/dashboard";
    }
	
	
	@GetMapping(value={"/athlete/seeAllGyms"})
	public String seeAllGyms(Authentication authentication,Model model) {
		List<Gym> gyms=gymService.getGyms();
		model.addAttribute("gyms",gyms);
		return "athlete/seeAllGyms";
	}

	@GetMapping(value={"/athlete/GymsThatIAmMember"})
	public String seeGymsThatIamMember(Authentication authentication,Model model) {
		 String athleteEmail = authentication.getName();
		 Athletes athlete = athleteService.getAthlete(athleteEmail);
		 Set<Gym> gyms = athlete.getGyms();
		 model.addAttribute("gyms", gyms);
		 return "athlete/gymsThatIAmMember";
	}
	
	@GetMapping("/athlete/history")
	public String viewAttendanceHistory(Model model, Authentication authentication, @RequestParam(name = "gymId") Long gymId) {
	    Athletes athlete = athleteService.getAthlete(authentication.getName());
	    Gym gym = gymService.getGymById(gymId);
	    List<ClassOfSchedule> attendedClasses = athleteService.findClasses(athlete);    
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
	    Athletes athlete = athleteService.getAthlete(authentication.getName());
	    Gym gym = gymService.getGymById(gymId);
	    ClassOfSchedule classOfSchedule = classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
	    Instructor instructor=classOfSchedule.getInstructor();
	    ClassRating classRating = new ClassRating();
	    classRating.setGym(gym);
	    classRating.setAthlete(athlete);
	    classRating.setClassOfSchedule(classOfSchedule);
	    classRating.setInstructorRating(instructorRating);
	    classRating.setAccuracyRating(accuracyRating);
	    classRating.setCrowdingRating(crowdingRating);
	    classRating.setInstructor(instructor);
	    classRatingService.saveClassRating(classRating);
	    
	    return "redirect:/athlete/history";
	}


}
