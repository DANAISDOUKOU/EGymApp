package dipl.danai.app.controller;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOccurrence;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Times;
import dipl.danai.app.model.WaitingListEntry;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.ClassOccurrenceRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.RoomRepository;
import dipl.danai.app.repository.ScheduleRepository;
import dipl.danai.app.repository.WaitingListEntryRepository;
import dipl.danai.app.repository.WorkoutRepository;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.EmailService;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.InstructorService;
import dipl.danai.app.service.ScheduleService;
import dipl.danai.app.service.TimeService;
import dipl.danai.app.service.WorkoutService;

@Controller
@RequestMapping("/gym")
public class ScheduleController {
	
	@Autowired
	WorkoutRepository workoutRepository;
	
	@Autowired
	GymRepository gymRepository;
	
	@Autowired
	InstructorRepository instructorRepository;
	
	@Autowired 
	ScheduleService scheduleService;
	@Autowired
	ScheduleRepository scheduleRepo;
	
	@Autowired
	InstructorService instructorService;
	
	@Autowired
	TimeService timeService;
	
	@Autowired
	GymService gymService;
	
	@Autowired
	WorkoutService workoutService;
	
	@Autowired
	ClassOfScheduleService classOfScheduleService;
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	ClassOfScheduleRepository classOfScheduleRepository;
	@Autowired
	AthleteRepository athleteRepository;
	@Autowired
	ClassOccurrenceRepository classOccurrenceRepository;
	@Autowired
	WaitingListEntryRepository waitingListEntryRepository;
	@Autowired
	EmailService emailService;
	
	@GetMapping(value = {"/addClass"})
	public String addClass(HttpServletRequest request,HttpSession session,@RequestParam(value = "programId", required = false) Long programId,
Model model,Authentication authentication) {
		session.setAttribute("callAddClass",request.getHeader("Referer"));
		String email=authentication.getName();
		Gym gym=gymRepository.findByEmail(email);
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		model.addAttribute("workouts",gym.getGymWorkouts());
		model.addAttribute("instructors",gym.getGymInstructors());	
		model.addAttribute("rooms", gym.getRooms());
		model.addAttribute("programId",programId);
		return "gym/addClass";
	}
	 
	@PostMapping(value = {"/addClass"})
	public String getClass(Authentication authentication,Model model, @Valid Schedule schedule,@RequestParam(value="workouts") String workout,
			@RequestParam(value="instructors") String instructor,
			@RequestParam(value="start_time") String start_time,
			@RequestParam(value="end_time")  String end_time,
			@RequestParam(value="rooms") String name,
			@RequestParam(value="capacity") int capacity,
			@RequestParam(value = "programId", required = false) Long programId,
			HttpSession session,
			BindingResult bindingResult)throws SQLException {
		String previousUrl=(String) session.getAttribute("callAddClass");
		session.removeAttribute("previousUrl");
		Times time=new Times();
		String email=authentication.getName();
		Gym gym=gymRepository.findByEmail(email);
		model.addAttribute("workouts",gym.getGymWorkouts() );
		model.addAttribute("instructors",gym.getGymInstructors());
		Room selectedRoom = roomRepository.findByNameAndGymId(name,gym.getGym_id());
		Workout w=workoutRepository.findByName(workout);
		Instructor i=instructorRepository.findByName(instructor);
		ClassOfSchedule c=new ClassOfSchedule();
		c.setRoom(selectedRoom);
		c.setWorkout(w);
		time.setTime_start(Time.valueOf(start_time+":00"));
		time.setTime_end(Time.valueOf(end_time+":00"));
		Times tim=timeService.saveTime(time);
		c.setTime(tim);
		c.setInstructor(i);
		c.setCapacity(capacity);
		classOfScheduleService.save(c);
		if (scheduleService.isRoomOccupied(selectedRoom, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"))) {
			 gym = selectedRoom.getGym();
			 bindingResult.rejectValue("gyms", "error.gyms", "The selected room at " + gym.getGym_name() + " is already occupied at this time.");
	    }
	    Instructor selectedInstructor = instructorRepository.findByName(instructor);
	    if (scheduleService.isInstructorOccupied(selectedInstructor, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"))) {
			bindingResult.rejectValue("gyms", "error.gyms", "The selected instructor at " + gym.getGym_name() + " is already occupied at this time.");
	    }
		scheduleService.saveClass(c);
		instructorService.saveClass(c);
		instructorService.saveInstructors(i);
		model.addAttribute("successMessage", "Class added to FitnessProgram!");
		if(previousUrl.equals("http://localhost:8080/gym/updateProgram")) {
			System.out.println("ahhahaha");
			System.out.println(programId);
			Schedule schedulee=scheduleRepo.findById(programId).orElse(null);
			List<ClassOfSchedule> classes=schedulee.getScheduleClasses();
			classes.add(c);
			schedule.setScheduleClasses(classes);
			scheduleRepo.save(schedulee);
		}
		if (bindingResult.hasErrors()) {
	        model.addAttribute("workouts", gym.getGymWorkouts() );
	        model.addAttribute("instructors",gym.getGymInstructors());
	        model.addAttribute("rooms", gym.getRooms());
	        return "gym/addClass";
	    }
		return "redirect:"+previousUrl;
	}
	
	@GetMapping(value = {"/createProgram"})
	public String createProgram(Model model) {
		model.addAttribute("fitnessProgram",new Schedule());
		return "gym/createProgram";
	}

	@PostMapping(value = {"/createProgram"})
	public String getProgram(Model model, @Valid Schedule schedule,
			Authentication authentication, 
			BindingResult bindingResult,
			@RequestParam(value="date") java.sql.Date startDate)throws SQLException, ParseException {
		Gym gym=gymRepository.findByEmail(authentication.getName());
		schedule.setWork_out_date(startDate);
		scheduleService.saveSchedule(schedule);
		gymService.saveProgram(schedule);
		gymService.saveGym(gym);
		return "gym/createProgram";
	}
	
	
	@PostMapping(value = {"/updateClass"})
	public String getupgradeClass(@ModelAttribute("workout") Workout workout,Model model) {
		model.addAttribute("workout", workout);
		return "gym/updateClass";
	}
	
	@PostMapping("/participate")
	@Transactional
	public String participateteInClass(@RequestParam("classOfScheduleId") Long classOfScheduleId,Authentication authentication,HttpServletRequest request,Model model) {
		String previousPageUrl = request.getHeader("Referer");
		Athletes a=athleteRepository.findByEmail(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
		if (authentication.getAuthorities().stream()
		        .anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null && classOfSchedule.getParticipants().size() < classOfSchedule.getCapacity()) {
				if(!classOfSchedule.getParticipants().contains(a)) {
					classOfScheduleService.addParicipant(a, classOfSchedule);
				}else {
					model.addAttribute("alreadySelected",true);
				}
			 }
		}
		classOfScheduleService.save(classOfSchedule);
		return "redirect:"+previousPageUrl;
	}
	
	 @PostMapping("/cancelPosition")
	 @Transactional
	 public String cancelPosition(@RequestParam("classOfScheduleId") Long classOfScheduleId, Authentication authentication, Model model,HttpServletRequest request) {
		String previousPageUrl = request.getHeader("Referer");
		Athletes a=athleteRepository.findByEmail(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
		if (authentication.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null) {
				if(classOfSchedule.getParticipants().contains(a)) {
					classOfScheduleService.removeParticipant(a, classOfSchedule);
					classOfScheduleService.save(classOfSchedule);
					
					List<WaitingListEntry> waitingListEntries = waitingListEntryRepository.findByClassOfSchedule(classOfSchedule);
					if (!waitingListEntries.isEmpty()) {
			            WaitingListEntry firstInWaitingList = waitingListEntries.get(0);
			            classOfSchedule.getParticipants().add(firstInWaitingList.getAthlete());
			            waitingListEntryRepository.delete(firstInWaitingList);
			            String notificationContent = "You have been moved from the waiting list to participants for the class: " +classOfSchedule.getTime().getTime_start()+" - "+classOfSchedule.getTime().getTime_end()+" "+ classOfSchedule.getWorkout().getName();
	                    emailService.sendEmail(firstInWaitingList.getAthlete().getEmail(), "Class Notification", notificationContent);
			        }
				}
			}
		}
	    model.addAttribute("alreadySelected", false);
		classOfScheduleService.save(classOfSchedule);
	    return "redirect:"+previousPageUrl;
	 }
	
	 @GetMapping("/class-schedule-details/{classOfScheduleId}")
	    public String getClassScheduleDetails(@PathVariable Long classOfScheduleId, Model model ,Authentication authentication, @RequestParam(required = false) Long gymId) {
		 Gym gym;
		 String role = authentication.getAuthorities().stream()
                 .findFirst()
                 .map(GrantedAuthority::getAuthority)
                 .orElse("");
		 if("GYM".equals(role)) {
			gym=gymRepository.findByEmail(authentication.getName());
		 }else {
			gym=gymRepository.findById(gymId).orElse(null);
		 }
		 ClassOfSchedule classSchedule = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);
	        model.addAttribute("classSchedule", classSchedule);
	        model.addAttribute("gym",gym);
	        Set<Athletes> availableAthletes = gymRepository.findMembers(gym.getGym_id());
		 	List<Athletes> participants=classSchedule.getParticipants();
		 	availableAthletes = availableAthletes.stream()
		            .filter(athlete -> !participants.contains(athlete))
		            .collect(Collectors.toSet());
		    model.addAttribute("availableAthletes", availableAthletes);
	        return "gym/classOfScheduleDetails";
	 }
	 
	 @PostMapping("/add-participant")
	 public String addParticipant(@RequestParam Long classOfScheduleId,
	                              @RequestParam("selectedAthletes") List<Long> selectedAthleteIds,
	                              @RequestParam Long gymId,Authentication authentication) {
		 String role = authentication.getAuthorities().stream()
                 .findFirst()
                 .map(GrantedAuthority::getAuthority)
                 .orElse("");
	    ClassOfSchedule c=classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
		 if(selectedAthleteIds!=null) {
			 for(Long athleteId:selectedAthleteIds) {
				 Athletes athlete=athleteRepository.findById(athleteId).orElse(null);
				 classOfScheduleService.addParicipant(athlete,c);
			 }
		 }
		 classOfScheduleService.save(c);
		 if("INSTRUCTOR".equals(role)) {
		 		return "redirect:/gym/class-schedule-details/" + classOfScheduleId+"?gymId="+gymId;
		 	}else {
		 		return "redirect:/gym/class-schedule-details/" + classOfScheduleId;
		 	}
	 }

	 @PostMapping("/remove-participant")
	   public String removeParticipant( @RequestParam Long gymId,@RequestParam Long classOfScheduleId, @RequestParam("removedParticipants") List<Long> removedParticipantIds,Authentication authentication) {
		 	String role = authentication.getAuthorities().stream()
                 .findFirst()
                 .map(GrantedAuthority::getAuthority)
                 .orElse("");
		 	ClassOfSchedule classSchedule = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);
		 	List<Athletes> participants = classSchedule.getParticipants();
		 	participants.removeIf(participant -> removedParticipantIds.contains(participant.getAthlete_id()));
		 	classSchedule.setParticipants(participants);
		 	 classOfScheduleService.save(classSchedule);
		 	if("INSTRUCTOR".equals(role)) {
		 		return "redirect:/gym/class-schedule-details/" + classOfScheduleId+"?gymId="+gymId;
		 	}else {
		 		return "redirect:/gym/class-schedule-details/" + classOfScheduleId;
		 	}
	    }
	 
	 @GetMapping("/deleteClass")
	 public String showDeleteClassPage(@RequestParam(value = "programId") Long programId,Model model) {
		 Schedule schedule = scheduleRepo.findById(programId).orElse(null);
		List<ClassOfSchedule> workoutList = schedule.getScheduleClasses();
		 model.addAttribute("program", schedule);
		 model.addAttribute("workoutList", workoutList);
		 return "gym/deleteClass";
	 }
	 
	 @PostMapping("/deleteClass")
	 public String deleteSelectedClasses(@RequestParam(value = "programId") Long programId,
	                                     @RequestParam(value = "selectedClasses", required = false) List<Long> selectedClassIds,
	                                     HttpSession session) {
		Schedule schedule = scheduleRepo.findById(programId).orElse(null);
		List<ClassOfSchedule> workoutList = schedule.getScheduleClasses();
	    if (selectedClassIds != null) {
	        for (Long classId : selectedClassIds) {
	            workoutList.removeIf(classOfSchedule -> selectedClassIds.contains(classOfSchedule.getClassOfScheduleId()));
	         }
	     }
	    schedule.setScheduleClasses(workoutList);
	    scheduleRepo.save(schedule);
	     String previousUrl = (String) session.getAttribute("callAddClass");
	     return "gym/deleteClass";
	 }
	 
	 @GetMapping("/modifyClass")
	 public String modifyClass(@RequestParam("classId") Long classId, Model model,Authentication authentication,@RequestParam(value="gymId",required=false) Long gymId,@RequestParam(value="scheduleId",required=false) Long scheduleId ) {
	    ClassOfSchedule classOfSchedule=classOfScheduleRepository.findById(classId).orElse(null);
	    Gym gym=gymRepository.findById(gymId).orElse(null);
	    model.addAttribute("classOfSchedule", classOfSchedule);
	    model.addAttribute("workouts",gym.getGymWorkouts());
	    model.addAttribute("instructors",gym.getGymInstructors());
	    model.addAttribute("rooms",gym.getRooms());
	    ClassOccurrence classOccurrence=classOccurrenceRepository.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(gymId, classId);
	    model.addAttribute("classOccurrence",classOccurrence);
	    model.addAttribute("scheduleId",scheduleId);
	    return "gym/modifyClass";
	 }
	 
	 @PostMapping("/performModification")
	 public String performModification(
	            @RequestParam("classId") Long classId,
	            @RequestParam("timeStart") Time timeStart,
	            @RequestParam("timeEnd") Time timeEnd,
	            @RequestParam("workoutId") Long workoutId,
	            @RequestParam("instructorId") Long instructorId,
	            @RequestParam("roomId") Long roomId,
	            @RequestParam(value = "canceled", required = false, defaultValue = "false") boolean canceled,
	            Model model,
	            HttpServletRequest request) {
		 	String referringPageUrl = request.getHeader("referer");
	        ClassOfSchedule classOfSchedule = classOfScheduleRepository.findById(classId).orElse(null);
	        ClassOfSchedule originalClassOfSchedule=classOfSchedule;
	        if (classOfSchedule != null) {
	            classOfSchedule.getTime().setTime_start(timeStart);
	            classOfSchedule.getTime().setTime_end(timeEnd);
	            Workout workout = workoutRepository.findById(workoutId).orElse(null);
	            if (workout != null) {
	                classOfSchedule.setWorkout(workout);
	            }
	            if (!instructorId.equals(classOfSchedule.getInstructor().getInstructor_id())) {
	                Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
	                if (instructor != null) {
	                    if (scheduleService.isInstructorOccupied(instructor, timeStart, timeEnd)) {
	                        model.addAttribute("instructorError", "The selected instructor is already occupied at this time.");
	                        return "redirect:" + referringPageUrl;
	                    }
	                    classOfSchedule.setInstructor(instructor);
	                }
	            }
	            if (!roomId.equals(classOfSchedule.getRoom().getRoomId())) {
	                Room room = roomRepository.findById(roomId).orElse(null);
	                if (room != null) {
	                    if (scheduleService.isRoomOccupied(room, timeStart, timeEnd)) {
	                        model.addAttribute("roomError", "The selected room is already occupied at this time.");
	                        return "redirect:" + referringPageUrl;
	                    }
	                    classOfSchedule.setRoom(room);
	                }
	            }
	           
	            classOfScheduleRepository.save(classOfSchedule);
	            for (Athletes participant : classOfSchedule.getParticipants()) {
	                String emailContent = "Class modification notification:\n\n"
	                        + "Class details after modification:\n"  + classOfSchedule.getTime().getTime_start()+" - "+classOfSchedule.getTime().getTime_end()+" \nWorkout: "+classOfSchedule.getWorkout().getName()+" \n Instructor:" + classOfSchedule.getInstructor().getInstructor_name()+"\nRoom: "+classOfSchedule.getRoom().getRoomName()+"\n\n";
	                emailService.sendEmail(participant.getEmail(), "Class Modification Notification", emailContent);
	            }
	        }

	        return "redirect:" + referringPageUrl;
	    }
	 
	 @PostMapping("/cancelClassOccurrence")
	 public String cancelClassOccurrence(
	         @RequestParam(value="classOccurrenceId",required=false) Long classOccurrenceId,
	         @RequestParam("scheduleId") Long scheduleId,
	         @RequestParam("classId") Long classId,
	         HttpServletRequest request) {
		 String referringPageUrl = request.getHeader("referer");
	 
	   ClassOccurrence classOcc=new  ClassOccurrence ();
	   classOcc.setSchedule(scheduleRepo.findById(scheduleId).orElse(null));
	   classOcc.setClassOfSchedule(classOfScheduleRepository.findById(classId).orElse(null));
	   classOcc.setCanceled(true);
	   classOccurrenceRepository.save(classOcc);
	     
	     return "redirect:" + referringPageUrl;
	 }
	 
	 @PostMapping("/uncancelClassOccurrence")
	 public String uncancelClassOccurrence(@RequestParam Long classId, @RequestParam("scheduleId") Long scheduleId,@RequestParam(value="classOccurrenceId",required=false) Long classOccurrenceId,HttpServletRequest request) {
		 String referringPageUrl = request.getHeader("referer"); 
		 ClassOccurrence classOccurrence = classOccurrenceRepository.findByScheduleScheduleIdAndClassOfScheduleClassOfScheduleId(scheduleId, classId);
		 List<ClassOccurrence> occurrences=classOccurrenceRepository.findAll();
		 ClassOfSchedule classOfSchedule = classOfScheduleRepository.findById(classId).orElse(null);
	        if (classOccurrence != null) {
	        	occurrences.remove(classOccurrence);
	        	 classOccurrenceRepository.delete(classOccurrence);
	            classOfSchedule.setIs_canceled(false);
	            classOfScheduleRepository.save(classOfSchedule);
	        }
	        return "redirect:" + referringPageUrl;	 
	  }
	 
	 @PostMapping("/joinWaitingList")
	 @Transactional
	 public String joinWaitingList(@RequestParam("classOfScheduleId") Long classOfScheduleId, Authentication authentication, Model model, HttpServletRequest request) {
		Athletes a=athleteRepository.findByEmail(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleRepository.findById(classOfScheduleId).orElse(null);	
		if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null) {
		        WaitingListEntry entry = new WaitingListEntry();
		        entry.setClassOfSchedule(classOfSchedule);
		        entry.setAthlete(a);
		        entry.setJoinedAt(LocalDateTime.now());
		        waitingListEntryRepository.save(entry);
		        model.addAttribute("isInWaitingList", true);
		    }
		}
		
	    String previousPageUrl = request.getHeader("Referer");
	    return "redirect:" + previousPageUrl;
	 }

}
