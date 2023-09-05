package dipl.danai.app.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Schedule;
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
    public String athletePage(Model model, Authentication authentication){
    	 Athletes athlete = athleteService.getAthlete(authentication.getName());
    	    List<Gym> gymsInCity = gymService.getGymsByCity(athlete.getCity());
    	    int numRandomGymsToShow = 3;
    	    Collections.shuffle(gymsInCity);
    	    List<Gym> randomGyms = gymsInCity.stream().limit(numRandomGymsToShow).collect(Collectors.toList());
    	    List<Map<String, Object>> gymInfoList = new ArrayList<>();
    	    Random random = new Random();
    	    for (Gym gym : randomGyms) {
    	        Map<String, Object> gymInfo = new HashMap<>();
    	        gymInfo.put("gymName", gym.getGym_name());
    	        gymInfo.put("gymId", gym.getGym_id());
    	        List<Map<String, Object>> membershipInfoList = new ArrayList<>();
    	        for (MembershipType membershipType : gym.getGym_memberships()) {
    	            int randomDiscount = random.nextInt(21); // Random discount between 0 and 20
    	            float discountedAmount = membershipType.getMembership_amount() * (1 - randomDiscount / 100.0f);

    	            Map<String, Object> membershipInfo = new HashMap<>();
    	            membershipInfo.put("membershipType", membershipType.getMembership_type_name());
    	            membershipInfo.put("discount", randomDiscount);
    	            membershipInfo.put("amount", membershipType.getMembership_amount());
    	            membershipInfo.put("discountedAmount", discountedAmount);
    	            membershipInfo.put("membershipId", membershipType.getMembership_type_id());
    	            membershipInfoList.add(membershipInfo);
    	        }
    	        gymInfo.put("memberships", membershipInfoList);
    	        gymInfoList.add(gymInfo);
    	    }

    	    model.addAttribute("gymInfoList", gymInfoList);
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
	    Map<LocalDate, List<ClassOfSchedule>> classesByDate = new HashMap<>();
	    
	    for (ClassOfSchedule classOfSchedule : attendedClassesInGym) {
	        for (Schedule schedule : classOfSchedule.getSchedules()) {
	            LocalDate date = schedule.getWork_out_date().toLocalDate();
	            classesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(classOfSchedule);
	        }
	    }
	    model.addAttribute("gym",gym);
	    model.addAttribute("attendedClasses", attendedClassesInGym);
	    model.addAttribute("classesByDate", classesByDate);
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
