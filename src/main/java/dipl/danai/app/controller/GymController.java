package dipl.danai.app.controller;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.Workout;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.GymRatingService;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.InstructorService;
import dipl.danai.app.service.MembershipService;
import dipl.danai.app.service.PaymentService;
import dipl.danai.app.service.ScheduleService;
import dipl.danai.app.service.WorkoutService;

@Controller
public class GymController {

	@Autowired
	GymService gymService;
	@Autowired
	InstructorService instructorService;
	@Autowired
	ClassOfScheduleService classOfScheduleService;
	@Autowired
	GymRatingService gymRatingService;
	@Autowired
	AthleteService athleteService;
	@Autowired
	ScheduleService scheduleService;
	@Autowired 
	MembershipService membershipService;
	@Autowired
	WorkoutService workoutService;
	@Autowired
	PaymentService paymentService;
	
	@GetMapping(value = {"/gym/dashboard"})
    public String gymPage(Authentication authentication){
        return "gym/dashboard";
    }
    
    @GetMapping(value={"/gym/createRoom"})
    public String gymRoom(Authentication authentication,Model model) {
    	 model.addAttribute("room",new Room());
		return "/gym/createRoom";
    }
    @PostMapping(value={"/gym/createRoom"})
    public String createGymRoom(Model model,Authentication authentication, @Valid Room room, BindingResult bindingResult) {
    	String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
    	gymService.addRoomToGym(gym,room);
		return "/gym/createRoom";
    }
     
    @GetMapping(value = {"/gym/program"})
    public String gymFitnessProgram(Authentication authentication,Model model){
    	String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email);
		System.out.println(gym.getRooms().size());
		model.addAttribute("rooms", gym.getRooms());
		Collection<Schedule> programList=gym.getGymSchedules();
		model.addAttribute("gym",gym);
		model.addAttribute("programList", programList);
		if(programList!=null) {
			Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
			for (Schedule p:programList) {
				Schedule program=scheduleService.getScheduleById(p.getSchedule_id());
				Collection<ClassOfSchedule> classes= program.getScheduleClasses();
				Date date=program.getWork_out_date();
				List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
				for(ClassOfSchedule classOfSchedule:classes) {
					classOfScheduleService.checkIfCanceled(p, classOfSchedule);
					classesOnDate.add(classOfSchedule);				
				}
				classesByDate.put(date, classesOnDate);
			}
			model.addAttribute("workoutList", classesByDate);
		}
        return "gym/program";
    }
        
	@GetMapping(value = {"/gym/updateProgram"})
    public String updateFitnessProgram(Authentication authentication,Model model){
    	String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email);
		Collection<Schedule> programList=gym.getGymSchedules();
		model.addAttribute("gym",gym);
		model.addAttribute("programList", programList);
		Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
		for(Schedule p:programList) {
			Schedule program=scheduleService.getScheduleById(p.getSchedule_id());
			Collection<ClassOfSchedule> classes= program.getScheduleClasses();
			Date date=program.getWork_out_date();
			List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
			for(ClassOfSchedule classOfSchedule:classes) {
				classOfScheduleService.checkIfCanceled(p, classOfSchedule);
				classesOnDate.add(classOfSchedule);				
			}
			classesByDate.put(date, classesOnDate);
		}
		
		model.addAttribute("workoutList", classesByDate);
    	return "gym/updateProgram";
    }

    
	@GetMapping("/gym/{id}")
	public String viewGymDetails(@PathVariable Long id, Model model,Authentication authentication) {
		 Gym gym = gymService.getGymById(id);
		 model.addAttribute("gym", gym);
		 boolean alreadyMember=gymService.checkIfAlreadyMember(id, authentication.getName());
		 boolean hasAlreadyMembershipType=gymService.checkIfHasSpecificMembership(id, authentication.getName(), alreadyMember);
		 model.addAttribute("alreadyMember",alreadyMember);
		 model.addAttribute("hasAlreadyMembershipType",hasAlreadyMembershipType);
		 model.addAttribute("memberships", gym.getGym_memberships());
		 Athletes athlete=athleteService.getAthlete(authentication.getName());
		 if(hasAlreadyMembershipType) {
			 MembershipType existingMembership=gymService.findExistingMembership(gym.getGym_id(),athlete.getAthlete_id());
			 model.addAttribute("existingMembership", existingMembership);
			 if ("lessons".equalsIgnoreCase(existingMembership.getMembership_type_name())) {
	                int remainingLessons = existingMembership.getRemainingLessons();
	                List<ClassOfSchedule> attendedClasses = athleteService.findClasses(athlete); 
	                List<ClassOfSchedule> attendedClassesInGym = attendedClasses.stream()
	        	            .filter(classOfSchedule -> classOfSchedule.getSchedules().stream()
	        	                    .anyMatch(schedule -> schedule.getGyms().contains(gym)))
	        	            .collect(Collectors.toList());
	                int attendedClassCount=attendedClassesInGym.size();
	                model.addAttribute("remainingLessons", remainingLessons - attendedClassCount);
	            }
			 else {
				 model.addAttribute("remainingLessons", null);
			 }
			 if ("months".equalsIgnoreCase(existingMembership.getMembership_type_name())) {
				 Membership membership=membershipService.findMembership(existingMembership.getMembership_type_id(), athlete.getAthlete_id());
				 model.addAttribute("expireAt", membership.getEndDate());
			 }
			 else {
				 model.addAttribute("expireAt", null);
			 }
		 }else {
			 model.addAttribute("existingMembership", null);
		 }	 
		 Collection<Schedule> programList=gym.getGymSchedules();
	     model.addAttribute("programList", gym.getGymSchedules());
	     if(programList!=null) {
				Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
				for (Schedule p:programList) {
					Schedule program=scheduleService.getScheduleById(p.getSchedule_id());
					Collection<ClassOfSchedule> classes= program.getScheduleClasses();
					Date date=program.getWork_out_date();
					List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
					for(ClassOfSchedule classOfSchedule:classes) {
						if(!classOfSchedule.getParticipants().contains(athlete)) {
							model.addAttribute("alreadySelected",false);
						}else {
							model.addAttribute("alreadySelected",true);
						}
						classOfScheduleService.checkIfCanceled(p, classOfSchedule);
						classesOnDate.add(classOfSchedule);				
					}
					classesByDate.put(date, classesOnDate);
				}
				model.addAttribute("workoutList", classesByDate);
			}
	    return "gym/details";
	}
	
	@GetMapping( "/gym/details")
	public String showGymDetails(@RequestParam("gym_id") Long gymId, Model model,Authentication authentication) {
	    Gym gym = gymService.getGymById(gymId);
	    if (gym!=null) {
	        model.addAttribute("gym", gym);
	        Collection<Schedule> programList=gym.getGymSchedules();
	        model.addAttribute("programList",programList);
	        if(programList!=null) {
				Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
				for (Schedule p:programList) {
					Schedule program=scheduleService.getScheduleById(p.getSchedule_id());
					Collection<ClassOfSchedule> classes= program.getScheduleClasses();
					Date date=program.getWork_out_date();
					List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
					for(ClassOfSchedule classOfSchedule:classes) {
						classOfScheduleService.checkIfCanceled(p, classOfSchedule);
						classesOnDate.add(classOfSchedule);				
					}
					classesByDate.put(date, classesOnDate);
				}
				model.addAttribute("workoutList", classesByDate);
			}
	    }
	    return "gym/details";
	} 
	    
	@GetMapping("/gym/search")
	public String searchGyms(@RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String workoutType,
            @RequestParam(required = false) Integer bestRating,
            @RequestParam(required = false) String city, Model model) {
	        List<Gym> gyms =new ArrayList<Gym>();
	        List<Gym> searchResults = gymService.searchGyms(name, address, workoutType, bestRating,city);
	        gyms.addAll(searchResults);
	        model.addAttribute("gyms", gyms);
	        return "gym/list";
	 } 
	
	@GetMapping("/gym/seeMembers")
	public String getMembers(Model model,Authentication authentication) {
		String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
		Set<Athletes> members=gymService.findMembers(gym.getGym_id());
		model.addAttribute("members", members);
		return "gym/seeMembers";
	}
	
	@GetMapping("/gym/membershipTypes")
	public String MembershipTypes(Model model,Authentication authentication) {
		Gym gym=gymService.getGymByEmail(authentication.getName()); 
		List<MembershipType> membershipsList=gym.getGym_memberships();
		model.addAttribute("membershipList", membershipsList);
		return "gym/membershipTypes";
	}
	
	@PostMapping("gym/subscribe")
	public String subscribeToGym( HttpServletRequest request,Authentication authentication,@RequestParam("gymId") Long gymId) {
		String referringPageUrl = request.getHeader("referer");
		Gym gym = gymService.getGymById(gymId);
		Athletes athlete=athleteService.getAthlete(authentication.getName());
		if(gym!=null) {
			gym.getGymMembers().add(athlete);
			gymService.saveUpdatedGym(gym);
		}
		return "redirect:" + referringPageUrl;
	}
	
	@PostMapping("gym/selectMembership")
	public String selectMembership( HttpServletRequest request,Authentication authentication,@RequestParam("membershipId") Long membershipId,@RequestParam("gymId") Long gymId,@RequestParam(value="new_amount",required=false) Double newAmount) {
		String referringPageUrl = request.getHeader("referer");
		MembershipType membershipType=membershipService.findMembershiById(membershipId);
		Athletes athlete=athleteService.getAthlete(authentication.getName());
		Gym gym=gymService.getGymById(gymId);
		if(membershipType!=null) {
			Membership membership=new Membership();
			membership.setAthlete(athlete);
			membership.setMembershipType(membershipType);
			if ("months".equalsIgnoreCase(membershipType.getMembership_type_name())) {
				 LocalDate membershipStartDate = LocalDate.now(); 
		            int months = Integer.parseInt(membershipType.getMembership_period());
		            LocalDate membershipEndDate = membershipStartDate.plusMonths(months);
		            membership.setStartDate(membershipStartDate);
		            membership.setEndDate(membershipEndDate);
		            membershipService.saveMembership(membership);
		    } 
			else if ("lessons".equalsIgnoreCase(membershipType.getMembership_type_name())) {
				int remainingLessons = membershipType.getRemainingLessons();
				membershipService.saveMembership(membership);
			}
			if (newAmount != null) {
		        paymentService.makePayment(athlete, gym, newAmount);
		    }else{
		    	paymentService.makePayment(athlete, gym,membershipType.getMembership_amount());
		    }
		}
		return "redirect:" + referringPageUrl;
	}
	
	@GetMapping("gym/typeOfWorkouts")
	public String typeOfWorkouts(Model model,Authentication authentication) {
		String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
		model.addAttribute("workouts",workoutService.findAll());
		model.addAttribute("workoutGyms",gym.getGymWorkouts());
		return "gym/typeOfWorkouts";
	}
	
	@PostMapping("/addWorkoutsToGym")
	public String addWorkoutsToGym(@RequestParam(value = "selectedWorkouts", required = false) List<Long> selectedWorkouts,
    Authentication authentication) {
		 String email = authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
		 if (selectedWorkouts != null) {
			 for (Long workoutId : selectedWorkouts) {
			      Workout workout = workoutService.findById(workoutId);
			      gymService.addWorkout(workout); 
			  }
		 }
		 gymService.saveGym(gym);
		return "redirect:/gym/typeOfWorkouts";
	}
	@GetMapping("gym/MeetTheInstructors")
	public String instructorsGym(Authentication authentication, Model model) {
		String email = authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
		model.addAttribute("instructorsGyms", gym.getGymInstructors());
		model.addAttribute("instructors",instructorService.findAll());
		return "gym/MeetTheInstructors";
	}
	
	@PostMapping("/addInstrucotrToGym")
	public String addInstructorToGym(@RequestParam("selectedInstructors") List<Long> selectedInstructors, Authentication authentication) {
		String email = authentication.getName();
		Gym gym=gymService.getGymByEmail(email); 
		if(selectedInstructors!= null) {
			for(Long id:selectedInstructors) {
				Instructor instructor=instructorService.getById(id);
				gymService.addInstructor(instructor);
			}
		}
		gymService.saveGym(gym);
	    return "redirect:/gym/MeetTheInstructors";
	}
	
	@GetMapping("/gym/instructor{id}")
	public String viewGymDetailsInstructor(Authentication authentication, @PathVariable Long id, Model model) {
		Gym gym=gymService.getGymById(id); 
	    model.addAttribute("gym", gym);
	    model.addAttribute("gymId",id);
	    Instructor instructor = instructorService.getByEmail(authentication.getName());
	    Long instructorId = instructor.getInstructor_id();
	    model.addAttribute("instructorId", instructorId);
	    Collection<Schedule> schedules = gym.getGymSchedules();
	    Map<Schedule, List<ClassOfSchedule>> scheduleClassesMap = new HashMap<>();
	    for (Schedule schedule : schedules) {
	        if (schedule.getWork_out_date() != null) {
	            List<ClassOfSchedule> classesForInstructor = new ArrayList<>();
	            for (ClassOfSchedule classOfSchedule : schedule.getScheduleClasses()) {
					classOfScheduleService.checkIfCanceled(schedule, classOfSchedule);

	            	if (classOfSchedule.getInstructor().getInstructor_id().equals(instructorId)) {
	                    classesForInstructor.add(classOfSchedule);
	                }
	            }
	            if (!classesForInstructor.isEmpty()) {
	                scheduleClassesMap.put(schedule, classesForInstructor);
	            }
	        }
	    }
	  
	    model.addAttribute("scheduleClassesMap", scheduleClassesMap);
	    return "gym/instructor-classes-details";
	}

	@GetMapping("/gym/instructor-classes-details")
	public String viewGymInstructorDetails(Model model, @ModelAttribute("gym") Gym gym, @ModelAttribute("instructorId") Long instructorId, @ModelAttribute("gymId") Long gymId) {
	    model.addAttribute("gym", gym);
	    List<ClassOfSchedule> classes = instructorService.getClassesByGym(instructorId, gymId);
	    model.addAttribute("classes", classes);

	    return "gym/instructor-classes-details";
	}
	
	 @PostMapping("/rate-gym/{gymId}")
	    public String rateGym(@PathVariable Long gymId, @RequestParam int rating, Authentication authentication) {
			Gym gym=gymService.getGymById(gymId); 
			Athletes athlete=athleteService.getAthlete(authentication.getName());
	        gymRatingService.addRating(gym, athlete, rating);
	        double currentTotalRatings = gymRatingService.calculateAverageRating(gym);
	        gym.setAverageRating(currentTotalRatings);
	        gymService.saveUpdatedGym(gym);
	        return "redirect:/gym/"+gymId+"?";
	    }
	 
	 @GetMapping("/workout/{workoutId}/class/{classOfScheduleId}/schedule/{scheduleId}")
	 public String showWorkoutDetails(@PathVariable Long workoutId, @PathVariable Long classOfScheduleId, @PathVariable Long scheduleId, Model model,Authentication authentication) {
		Workout workout=workoutService.findById(workoutId);
		Athletes athlete=athleteService.getAthlete(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		int position=-1;
		if (classOfSchedule != null) {
		    int entriesBeforeAthlete =classOfScheduleService.findPositionInWitingList(classOfSchedule, athlete);
		    position=entriesBeforeAthlete;
		 }
		 if (position > 0) {
		     model.addAttribute("position", position);
		     model.addAttribute("isInWaitingList", true);
		 } else {
		    model.addAttribute("position", -1);
		    model.addAttribute("isInWaitingList", false);
		 }
		if(!classOfSchedule.getParticipants().contains(athlete)) {
			model.addAttribute("alreadySelected",false);
		}else {
			model.addAttribute("alreadySelected",true);
		}
		if(classOfSchedule.getParticipants().size()==classOfSchedule.getCapacity() ) {
			model.addAttribute("allPositionsReserved", true);
		}else {
			model.addAttribute("allPositionsReserved", false);
		}
		model.addAttribute("workout",workout);   
		model.addAttribute("classOfSchedule",classOfSchedule);   

		return "gym/workoutDetails"; 
	 }
}