package dipl.danai.app.controller;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.WaitingListEntry;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.ScheduleRepository;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.ClassOccurrenceRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.GymRatingRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.MembershipTypeRepository;
import dipl.danai.app.repository.TimesRepository;
import dipl.danai.app.repository.WaitingListEntryRepository;
import dipl.danai.app.repository.WorkoutRepository;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.GymRatingService;
import dipl.danai.app.service.GymService;

@Controller
public class GymController {
	@Autowired
	GymRepository gymRepository;
	
	@Autowired
	GymRatingRepository gymRatingRepository;
	
	@Autowired
	ScheduleRepository scheduleRepository;
	@Autowired
	WorkoutRepository workoutRepository;
	@Autowired 
	TimesRepository timeRepository;
	@Autowired 
	AthleteRepository athleteRepository;
	@Autowired
	MembershipTypeRepository membershipRepository;
	@Autowired
	GymService gymService;
	@Autowired
	InstructorRepository instructorRepository;
	@Autowired
	ClassOfScheduleService classOfScheduleService;
	@Autowired
	GymRatingService gymRatingService;
	@Autowired
	ClassOccurrenceRepository classOccurenceRepo;
	@Autowired
	ClassOfScheduleRepository classOfScheduleRepository;
	@Autowired
	WaitingListEntryRepository waitingListEntryRepository;
	
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
		Gym gym=gymRepository.findByEmail(email); 
    	gymService.addRoomToGym(gym,room);
		return "/gym/createRoom";
    }
     
    @GetMapping(value = {"/gym/program"})
    public String gymFitnessProgram(Authentication authentication,Model model){
    	String email=authentication.getName();
		Gym gym=gymRepository.findByEmail(email); 
		System.out.println(gym.getRooms().size());
		model.addAttribute("rooms", gym.getRooms());
		Collection<Schedule> programList=gym.getGymSchedules();
		model.addAttribute("gym",gym);
		model.addAttribute("programList", programList);
		if(programList!=null) {
			Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
			for (Schedule p:programList) {
				Schedule program=scheduleRepository.findById(p.getSchedule_id()).orElse(null);
				Collection<ClassOfSchedule> classes= program.getScheduleClasses();
				Date date=program.getWork_out_date();
				List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
				for(ClassOfSchedule classOfSchedule:classes) {
					if(classOccurenceRepo.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(p.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
						classOfSchedule.setIs_canceled(true);
						classOfScheduleRepository.save(classOfSchedule);
						
					}
					else {
						classOfSchedule.setIs_canceled(false);
						classOfScheduleRepository.save(classOfSchedule);
					}
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
		Gym gym=gymRepository.findByEmail(email); 
		Collection<Schedule> programList=gym.getGymSchedules();
		model.addAttribute("gym",gym);
		model.addAttribute("programList", programList);
		Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
		for(Schedule p:programList) {
			Schedule program=scheduleRepository.findById(p.getSchedule_id()).orElse(null);
			Collection<ClassOfSchedule> classes= program.getScheduleClasses();
			Date date=program.getWork_out_date();
			List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
			for(ClassOfSchedule classOfSchedule:classes) {
				if(classOccurenceRepo.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(p.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
					classOfSchedule.setIs_canceled(true);
					classOfScheduleRepository.save(classOfSchedule);
				}
				else {
					classOfSchedule.setIs_canceled(false);
					classOfScheduleRepository.save(classOfSchedule);
				}
				classesOnDate.add(classOfSchedule);				
			}
			classesByDate.put(date, classesOnDate);
		}
		
		model.addAttribute("workoutList", classesByDate);
    	return "gym/updateProgram";
    }

    
	@GetMapping("/gym/{id}")
	public String viewGymDetails(@PathVariable Long id, Model model,Authentication authentication) {
		 Gym gym = gymRepository.findById(id).orElse(null);
		 model.addAttribute("gym", gym);
		 boolean alreadyMember=gymService.checkIfAlreadyMember(id, authentication.getName());
		 boolean hasAlreadyMembershipType=gymService.checkIfHasSpecificMembership(id, authentication.getName(), alreadyMember);
		 model.addAttribute("alreadyMember",alreadyMember);
		 model.addAttribute("hasAlreadyMembershipType",hasAlreadyMembershipType);
		 model.addAttribute("memberships", gym.getGym_memberships());
		 Athletes athlete=athleteRepository.findByEmail(authentication.getName());
		 //moveToService
		 if(hasAlreadyMembershipType) {
			 MembershipType existingMembership=gymRepository.findExistingMebership(gym.getGym_id(),athlete.getAthlete_id());
			 model.addAttribute("existingMembership", existingMembership);
		 }else {
			 model.addAttribute("existingMembership", null);
		 }	 
		 Collection<Schedule> programList=gym.getGymSchedules();
	     model.addAttribute("programList", gym.getGymSchedules());
	     if(programList!=null) {
				Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
				for (Schedule p:programList) {
					Schedule program=scheduleRepository.findById(p.getSchedule_id()).orElse(null);
					Collection<ClassOfSchedule> classes= program.getScheduleClasses();
					Date date=program.getWork_out_date();
					List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
					for(ClassOfSchedule classOfSchedule:classes) {
						if(!classOfSchedule.getParticipants().contains(athlete)) {
							model.addAttribute("alreadySelected",false);
						}else {
							model.addAttribute("alreadySelected",true);
						}
						if(classOccurenceRepo.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(p.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
							classOfSchedule.setIs_canceled(true);
							classOfScheduleRepository.save(classOfSchedule);
						}
						else {
							classOfSchedule.setIs_canceled(false);
							classOfScheduleRepository.save(classOfSchedule);
						}
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
	    Optional<Gym> optionalGym = gymRepository.findById(gymId);
	    Athletes athlete=athleteRepository.findByEmail(authentication.getName());
	    if (optionalGym.isPresent()) {
	        Gym gym = optionalGym.get();
	        model.addAttribute("gym", gym);
	        Collection<Schedule> programList=gym.getGymSchedules();
	        model.addAttribute("programList",programList);
	        if(programList!=null) {
				Map<Date,List<ClassOfSchedule>> classesByDate = new HashMap<>(); 
				for (Schedule p:programList) {
					Schedule program=scheduleRepository.findById(p.getSchedule_id()).orElse(null);
					Collection<ClassOfSchedule> classes= program.getScheduleClasses();
					Date date=program.getWork_out_date();
					List<ClassOfSchedule> classesOnDate=new ArrayList<ClassOfSchedule>();
					for(ClassOfSchedule classOfSchedule:classes) {
						if(classOccurenceRepo.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(p.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
							classOfSchedule.setIs_canceled(true);
							classOfScheduleRepository.save(classOfSchedule);	
						}
						else {
							classOfSchedule.setIs_canceled(false);
							classOfScheduleRepository.save(classOfSchedule);
						}
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
		Gym gym=gymRepository.findByEmail(email); 
		System.out.println(gym);
		Set<Athletes> members=gymRepository.findMembers(gym.getGym_id());
		model.addAttribute("members", members);
		return "gym/seeMembers";
	}
	
	@GetMapping("/gym/membershipTypes")
	public String MembershipTypes(Model model,Authentication authentication) {
		Gym gym=gymRepository.findByEmail(authentication.getName());
		List<MembershipType> membershipsList=gym.getGym_memberships();
		model.addAttribute("membershipList", membershipsList);
		return "gym/membershipTypes";
	}
	
	@PostMapping("gym/subscribe")
	public String subscribeToGym(Authentication authentication,@RequestParam("gymId") Long gymId) {
		Gym gym = gymRepository.findById(gymId).orElse(null);
		Athletes athlete=athleteRepository.findByEmail(authentication.getName());
		if(gym!=null) {//move to Service
			gym.getGymMembers().add(athlete);
			gymRepository.save(gym);
		}
		return "redirect:/success-page";
	}
	
	@PostMapping("gym/selectMembership")
	public String selectMembership(Authentication authentication,@RequestParam("membershipId") Long membershipId,@RequestParam("gymId") Long gymId) {
		MembershipType membership=membershipRepository.findById(membershipId).orElse(null);
		Athletes athlete=athleteRepository.findByEmail(authentication.getName());
		if(membership!=null) {//move to Service
			athlete.getMemberships().add(membership);
			athleteRepository.save(athlete);
		}
		return "redirect:/success-page";
	}
	
	@GetMapping("gym/typeOfWorkouts")
	public String typeOfWorkouts(Model model,Authentication authentication) {
		String email=authentication.getName();
		Gym gym=gymRepository.findByEmail(email);
		model.addAttribute("workouts",workoutRepository.findAll());
		model.addAttribute("workoutGyms",gym.getGymWorkouts());
		return "gym/typeOfWorkouts";
	}
	
	@PostMapping("/addWorkoutsToGym")
	public String addWorkoutsToGym(@RequestParam(value = "selectedWorkouts", required = false) List<Long> selectedWorkouts,
    Authentication authentication) {
		 String email = authentication.getName();
		 Gym gym = gymRepository.findByEmail(email);
		 if (selectedWorkouts != null) {
			    for (Long workoutId : selectedWorkouts) {
			      Workout workout = workoutRepository.findById(workoutId).orElse(null);
			      gymService.addWorkout(workout); 
			    }
			  }
		  gymService.saveGym(gym);
		return "redirect:/gym/typeOfWorkouts";
	}
	@GetMapping("gym/MeetTheInstructors")
	public String instructorsGym(Authentication authentication, Model model) {
		String email = authentication.getName();
		Gym gym = gymRepository.findByEmail(email);
		model.addAttribute("instructorsGyms", gym.getGymInstructors());
		model.addAttribute("instructors",instructorRepository.findAll());
		return "gym/MeetTheInstructors";
	}
	
	@PostMapping("/addInstrucotrToGym")
	public String addInstructorToGym(@RequestParam("selectedInstructors") List<Long> selectedInstructors, Authentication authentication) {
		String email = authentication.getName();
		Gym gym = gymRepository.findByEmail(email);
		if(selectedInstructors!= null) {
			for(Long id:selectedInstructors) {
				Instructor instructor=instructorRepository.findById(id).orElse(null);
				gymService.addInstructor(instructor);
			}
		}
		gymService.saveGym(gym);
	    return "redirect:/gym/MeetTheInstructors";
	}
	
	@GetMapping("/gym/instructor{id}")
	public String viewGymDetailsInstructor(Authentication authentication, @PathVariable Long id, Model model) {
	    Gym gym = gymRepository.findById(id).orElse(null);
	    model.addAttribute("gym", gym);
	    model.addAttribute("gymId",id);
	    Instructor instructor = instructorRepository.findByEmail(authentication.getName());
	    Long instructorId = instructor.getInstructor_id();
	    model.addAttribute("instructorId", instructorId);
	    Collection<Schedule> schedules = gym.getGymSchedules();
	    Map<Schedule, List<ClassOfSchedule>> scheduleClassesMap = new HashMap<>();
	    for (Schedule schedule : schedules) {
	        if (schedule.getWork_out_date() != null) {
	            List<ClassOfSchedule> classesForInstructor = new ArrayList<>();
	            for (ClassOfSchedule classOfSchedule : schedule.getScheduleClasses()) {
	            	if(classOccurenceRepo.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(schedule.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
						classOfSchedule.setIs_canceled(true);
						classOfScheduleRepository.save(classOfSchedule);	
					}
					else {
						classOfSchedule.setIs_canceled(false);
						classOfScheduleRepository.save(classOfSchedule);
					}
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
	    List<ClassOfSchedule> classes = instructorRepository.findClassesByInstructorAndGym(instructorId, gymId);
	    model.addAttribute("classes", classes);

	    return "gym/instructor-classes-details";
	}
	
	 @PostMapping("/rate-gym/{gymId}")
	    public String rateGym(@PathVariable Long gymId, @RequestParam int rating, Authentication authentication) {
	        Gym gym = gymRepository.findById(gymId).orElse(null);
	        Athletes athlete = athleteRepository.findByEmail(authentication.getName());
	        gymRatingService.addRating(gym, athlete, rating);
	        double currentTotalRatings = gymRatingRepository.calculateAverageRatingByGym(gym);
	        System.out.println("GYMMMMMM "+currentTotalRatings);
	        gym.setAverageRating(currentTotalRatings);
	        
	        gymRepository.save(gym); 
	        return "redirect:/gym/"+gymId+"?";
	    }
	 
	 @GetMapping("/workout/{workoutId}/class/{classOfScheduleId}/schedule/{scheduleId}")
	 public String showWorkoutDetails(@PathVariable Long workoutId, @PathVariable Long classOfScheduleId, @PathVariable Long scheduleId, Model model,Authentication authentication) {
		Workout workout=workoutRepository.findById(workoutId).orElse(null);
		Athletes athlete=athleteRepository.findByEmail(authentication.getName());
		Schedule program=scheduleRepository.findById(scheduleId).orElse(null);
		ClassOfSchedule classOfSchedule=classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
		int position=-1;
		if (classOfSchedule != null) {
		    int entriesBeforeAthlete = waitingListEntryRepository.countBeforeAthleteJoinedAt(classOfSchedule, athlete);
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