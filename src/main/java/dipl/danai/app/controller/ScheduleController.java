package dipl.danai.app.controller;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dipl.danai.app.model.AthleteClassScheduleReservation;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOccurrence;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.WaitingListEntry;
import dipl.danai.app.model.Workout;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.EmailService;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.InstructorService;
import dipl.danai.app.service.PopularityCalculationService;
import dipl.danai.app.service.ScheduleService;
import dipl.danai.app.service.WorkoutService;

@Controller
@RequestMapping("/gym")
public class ScheduleController {

	@Autowired
	ScheduleService scheduleService;

	@Autowired
	InstructorService instructorService;

	@Autowired
	GymService gymService;

	@Autowired
	WorkoutService workoutService;

	@Autowired
	AthleteService athleteService;
	@Autowired
	ClassOfScheduleService classOfScheduleService;

	@Autowired
	EmailService emailService;
	@Autowired
	PopularityCalculationService workoutPopularityService;
	
	

	@GetMapping(value = {"/addClass"})
	public String addClass(HttpServletRequest request,HttpSession session,@RequestParam(value = "programId", required = false) Long programId,
Model model,Authentication authentication) {
		session.setAttribute("callAddClass",request.getHeader("Referer"));
		String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email);
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
			BindingResult bindingResult)throws SQLException{
		String previousUrl=(String) session.getAttribute("callAddClass");
		session.removeAttribute("previousUrl");
		String email=authentication.getName();
		Gym gym=gymService.getGymByEmail(email);
		Room selectedRoom = gymService.getRoomByName(name,gym.getGym_id());
		Workout w=workoutService.getByName(workout);
		Instructor i=instructorService.getByName(instructor);
		ClassOfSchedule c=new ClassOfSchedule();
		c.setRoom(selectedRoom);
		c.setWorkout(w);
		c.setTime_start(Time.valueOf(start_time+":00"));
		c.setTime_end(Time.valueOf(end_time+":00"));
		c.setInstructor(i);
		c.setCapacity(capacity);
		classOfScheduleService.save(c);
		if(programId!=null) {
			Schedule schedulee=scheduleService.getScheduleById(programId);
			if (scheduleService.isRoomOccupied(selectedRoom, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"),schedulee)) {
				 gym = selectedRoom.getGym();
				 bindingResult.rejectValue("gyms", "error.gyms", "The selected room at " + gym.getGym_name() + " is already occupied at this time.");
		    }
		    if (scheduleService.isInstructorOccupied(i, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"),schedulee)) {
				bindingResult.rejectValue("gyms", "error.gyms", "The selected instructor at " + gym.getGym_name() + " is already occupied at this time.");
		    }
		}else {
			if (scheduleService.isRoomOccupiedInClasses(selectedRoom, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"))) {
				 gym = selectedRoom.getGym();
				 bindingResult.rejectValue("gyms", "error.gyms", "The selected room at " + gym.getGym_name() + " is already occupied at this time.");
		    }
		    Instructor selectedInstructor = instructorService.getByName(instructor);
		    if (scheduleService.isInstructorOccupiedInClasses(selectedInstructor, Time.valueOf(start_time + ":00"), Time.valueOf(end_time + ":00"))) {
				bindingResult.rejectValue("gyms", "error.gyms", "The selected instructor at " + gym.getGym_name() + " is already occupied at this time.");
		    }
		}
		scheduleService.saveClass(c);
		instructorService.saveClass(c);
		instructorService.saveInstructors(i);
		model.addAttribute("successMessage", "Class added to FitnessProgram!");
		if(previousUrl.equals("http://localhost:8080/gym/updateProgram")) {
			Schedule schedulee=scheduleService.getScheduleById(programId);
			List<ClassOfSchedule> classes=schedulee.getScheduleClasses();
			classes.add(c);
			schedulee.setScheduleClasses(classes);
			scheduleService.saveUpdatedSchedule(schedulee);
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
	        @RequestParam(value="date") String date,
	        @RequestParam(value = "weeks") int weeks) throws SQLException, ParseException {
	    Gym gym = gymService.getGymByEmail(authentication.getName());
	    Date startDate = Date.valueOf(date);
	    schedule.setWork_out_date(startDate);
	    schedule.setWeeks(weeks);
	    scheduleService.saveSchedule(schedule);
	    gym.getGymSchedules().add(schedule);
	    gymService.saveGym(gym);

	    for (int i = 1; i <= weeks - 1; i++) {
	        LocalDate newDate = startDate.toLocalDate().plusWeeks(i);
	        Schedule newSchedule = new Schedule();
	        newSchedule.setWeeks(weeks - i);
	        newSchedule.setWork_out_date(Date.valueOf(newDate));
	        List<ClassOfSchedule> classes = schedule.getScheduleClasses();
	        for (ClassOfSchedule c : classes) {
	            ClassOfSchedule newClass = new ClassOfSchedule(c.getWorkout(), c.getTime_start(), c.getTime_end(), c.getInstructor(), c.getRoom(), c.getCapacity(), false);
	            newSchedule.getScheduleClasses().add(newClass);
	        }
	        scheduleService.saveSchedule(newSchedule);
	        gym.getGymSchedules().add(newSchedule);
	        gymService.saveGym(gym);
	    }
	    return "redirect:/gym/createProgram";
	}


	@PostMapping("/participate")
	@Transactional
	public String participateteInClass(@RequestParam("workoutId") Long workoutId,
	        @RequestParam("scheduleId") Long scheduleId,
	        @RequestParam("gymId") Long gymId,
	        @RequestParam("classOfScheduleId") Long classOfScheduleId
	        ,Authentication authentication,HttpServletRequest request,Model model) {
		String previousPageUrl = request.getHeader("Referer");
		if(previousPageUrl.contains("?isModalOpen=true")) {
			previousPageUrl=previousPageUrl;
		}else {
			previousPageUrl = request.getHeader("Referer") + "?isModalOpen=true&workoutId=" + workoutId + "&scheduleId=" + scheduleId + "&gymId=" + gymId + "&classOfScheduleId=" + classOfScheduleId;
		}
		Athletes a=athleteService.getAthlete(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		Gym gym=gymService.getGymById(gymId);
		if (authentication.getAuthorities().stream()
		        .anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null && classOfSchedule.getParticipants().size() < classOfSchedule.getCapacity()) {
				if(!classOfSchedule.getParticipants().contains(a)) {
					classOfScheduleService.addParicipant(a, classOfSchedule);
					workoutPopularityService.recordUserInteraction(gym, classOfSchedule.getWorkout());
				}else {
					model.addAttribute("alreadySelected",true);
				}
			 }
		}
		classOfScheduleService.save(classOfSchedule);
		return "redirect:"+previousPageUrl;
	}
	
	@PostMapping("/participateNextWeeks")
	@Transactional
	public String participateteInClassNextWeeks(@RequestParam("workoutId") Long workoutId,
	        @RequestParam("scheduleId") Long scheduleId,
	        @RequestParam("gymId") Long gymId,
	        @RequestParam("classOfScheduleId") Long classOfScheduleId,
	        @RequestParam("weeksToReserve") Integer weeksToReserve, // Capture the selected number of weeks
	        Authentication authentication,
	        HttpServletRequest request,
	        Model model,
	        RedirectAttributes redirectAttributes) {
		String previousPageUrl=request.getHeader("Referer");
		if(previousPageUrl.contains("?isModalOpen=true")) {
			previousPageUrl=previousPageUrl;
		}else {
			previousPageUrl = request.getHeader("Referer") + "?isModalOpen=true&workoutId=" + workoutId + "&scheduleId=" + scheduleId + "&gymId=" + gymId + "&classOfScheduleId=" + classOfScheduleId;
		}
		Athletes a=athleteService.getAthlete(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		Gym gym=gymService.getGymById(gymId);
		Schedule schedule=scheduleService.getScheduleById(scheduleId);
		Date date=schedule.getWork_out_date();
		for(int i=1;i<=weeksToReserve;i++) {
			LocalDate newDate =date.toLocalDate().plusWeeks(i);
			Schedule s=scheduleService.getScheduleByDateAndGym(Date.valueOf(newDate), gym);
			List<ClassOfSchedule> classes =s.getScheduleClasses();
			for(ClassOfSchedule c:classes) {
				if(classOfScheduleService.checkIfSame(classOfSchedule,c)) {
					c.getParticipants().add(a);
					classOfScheduleService.save(c);
				}
			}
		}
		AthleteClassScheduleReservation reserve=new AthleteClassScheduleReservation(a,classOfSchedule,weeksToReserve);
		athleteService.saveReservetions(reserve);
		model.addAttribute("isModalOpen", true);
		return "redirect:"+previousPageUrl;
	}

	 @PostMapping("/cancelPosition")
	 @Transactional
	 public String cancelPosition(@RequestParam("workoutId") Long workoutId,
		        @RequestParam("scheduleId") Long scheduleId,
		        @RequestParam("gymId") Long gymId,
		        @RequestParam("classOfScheduleId") Long classOfScheduleId, Authentication authentication, Model model,HttpServletRequest request) {
		String previousPageUrl = request.getHeader("Referer");
		if(previousPageUrl.contains("?isModalOpen=true")) {
			previousPageUrl=previousPageUrl;
		}else {
			previousPageUrl = request.getHeader("Referer") + "?isModalOpen=true&workoutId=" + workoutId + "&scheduleId=" + scheduleId + "&gymId=" + gymId + "&classOfScheduleId=" + classOfScheduleId;
		}
		Athletes a=athleteService.getAthlete(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		if (authentication.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null) {
				if(classOfSchedule.getParticipants().contains(a)) {
					classOfScheduleService.removeParticipant(a, classOfSchedule);
					classOfScheduleService.save(classOfSchedule);
					List<WaitingListEntry> waitingListEntries = classOfScheduleService.getlist(classOfSchedule);
					if (!waitingListEntries.isEmpty()) {
			            WaitingListEntry firstInWaitingList = waitingListEntries.get(0);
			            classOfSchedule.getParticipants().add(firstInWaitingList.getAthlete());
			            classOfScheduleService.deleteFromWaitingList(firstInWaitingList);
			            String notificationContent = "You have been moved from the waiting list to participants for the class: " +classOfSchedule.getTime_start()+" - "+classOfSchedule.getTime_end()+" "+ classOfSchedule.getWorkout().getName();
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
			gym=gymService.getGymByEmail(authentication.getName());
		 }else {
			gym=gymService.getGymById(gymId);
		 }
		 ClassOfSchedule classSchedule = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);
	        model.addAttribute("classSchedule", classSchedule);
	        model.addAttribute("gym",gym);
	        Set<Athletes> availableAthletes = gymService.findMembers(gym.getGym_id());
		 	List<Athletes> participants=classSchedule.getParticipants();
		 	availableAthletes = availableAthletes.stream()
		            .filter(athlete -> !participants.contains(athlete))
		            .collect(Collectors.toSet());
		    model.addAttribute("availableAthletes", availableAthletes);
	        return "gym/classOfScheduleDetails";
	 }

	 @GetMapping("/select-members")
	 public String selectMembers(Model model,Authentication authentication,@RequestParam Long classOfScheduleId,@RequestParam Long gymId) {
		Gym	gym=gymService.getGymById(gymId);
		 Set<Athletes> availableAthletes = gymService.findMembers(gym.getGym_id());
		 ClassOfSchedule classSchedule = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);
		 	List<Athletes> participants=classSchedule.getParticipants();
		 	availableAthletes = availableAthletes.stream()
		            .filter(athlete -> !participants.contains(athlete))
		            .collect(Collectors.toSet());
		 model.addAttribute("availableAthletes", availableAthletes);
		 model.addAttribute("gymId",gymId);
		 model.addAttribute("classOfScheduleId",classOfScheduleId);
		  return "gym/select-members";
	 }

	 @GetMapping("/search-members")
	    public String searchMembers(@RequestParam String searchTerm, Model model,@RequestParam Long gymId,@RequestParam Long classOfScheduleId) {
		 Gym	gym=gymService.getGymById(gymId);
		 Set<Athletes> availableAthletes = gymService.findMembers(gym.getGym_id());
		 ClassOfSchedule classSchedule = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);
		 	List<Athletes> participants=classSchedule.getParticipants();
		 	availableAthletes = availableAthletes.stream()
		            .filter(athlete -> !participants.contains(athlete))
		            .collect(Collectors.toSet());
		 	List<Athletes> searchResults = gymService.searchMembers(searchTerm,availableAthletes);
	        model.addAttribute("searchResults", searchResults);
	        model.addAttribute("gymId", gymId);
	        model.addAttribute("classOfScheduleId",classOfScheduleId);
	        return "gym/select-members";
	    }

	 @PostMapping("/add-participant")
	 public String addParticipant(@RequestParam Long classOfScheduleId,
	                              @RequestParam("selectedAthletes") Long selectedAthleteIds,
	                              @RequestParam Long gymId,Authentication authentication) {
		 String role = authentication.getAuthorities().stream()
                 .findFirst()
                 .map(GrantedAuthority::getAuthority)
                 .orElse("");
		 ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		 if(selectedAthleteIds!=null) {
			 Athletes athlete=athleteService.getById(selectedAthleteIds);
			 classOfScheduleService.addParicipant(athlete,classOfSchedule);

		 }
		 classOfScheduleService.save(classOfSchedule);
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
		 Schedule schedule = scheduleService.getScheduleById(programId);
		 List<ClassOfSchedule> workoutList = schedule.getScheduleClasses();
		 workoutList.sort(Comparator.comparing(ClassOfSchedule::getTime_start));
		 model.addAttribute("program", schedule);
		 model.addAttribute("workoutList", workoutList);
		 return "gym/deleteClass";
	 }

	 @PostMapping("/deleteClass")
	 public String deleteSelectedClasses(@RequestParam(value = "programId") Long programId,
	                                     @RequestParam(value = "selectedClasses", required = false) List<Long> selectedClassIds,
	                                     HttpServletRequest request) {
		String previousPageUrl = request.getHeader("Referer");
		Schedule schedule = scheduleService.getScheduleById(programId);
		List<ClassOfSchedule> workoutList = schedule.getScheduleClasses();
	    if (selectedClassIds != null) {
	        for (Long classId : selectedClassIds) {
	            workoutList.removeIf(classOfSchedule -> selectedClassIds.contains(classOfSchedule.getClassOfScheduleId()));
	         }
	     }
	    schedule.setScheduleClasses(workoutList);
	    scheduleService.saveUpdatedSchedule(schedule);
	    return "redirect:" + previousPageUrl;
	 }

	 @GetMapping("/modifyClass")
	 public String modifyClass(@RequestParam("classId") Long classId, Model model,Authentication authentication,@RequestParam(value="gymId",required=false) Long gymId,@RequestParam(value="roomError",required=false) String roomError,@RequestParam(value="instructorError",required=false) String instructorError,@RequestParam(value="scheduleId",required=false) Long scheduleId, HttpSession session ) {
		 ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classId);
	    Gym gym=gymService.getGymById(gymId);
	    Schedule schedule=scheduleService.getScheduleById(scheduleId);
	    model.addAttribute("classOfSchedule", classOfSchedule);
	    model.addAttribute("workouts",gym.getGymWorkouts());
	    model.addAttribute("instructors",gym.getGymInstructors());
	    model.addAttribute("rooms",gym.getRooms());
	    ClassOccurrence classOccurrence=classOfScheduleService.getClassOccurrence(scheduleId, classId);
	    model.addAttribute("classOccurrence",classOccurrence);
	    model.addAttribute("schedule",schedule);
	    String userRole= authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
	    model.addAttribute("userRole", userRole);
	    if (instructorError != null && !instructorError.isEmpty()) {
	        model.addAttribute("instructorError", instructorError);
	    }
	    if (roomError != null && !roomError.isEmpty()) {
	        model.addAttribute("roomError", roomError);
	    }
	    model.addAttribute("gymId", gymId);
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
	            @RequestParam("scheduleId") Long scheduleId,
	            @RequestParam("gymId") Long gymId,
	            @RequestParam(value = "canceled", required = false, defaultValue = "false") boolean canceled,
	            Model model,
	            HttpServletRequest request) {
		 	String referringPageUrl = request.getHeader("referer");
			ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classId);
	        if (classOfSchedule != null) {
	            if (instructorId.equals(classOfSchedule.getInstructor().getInstructor_id())) {
	            	Instructor instructor = instructorService.getById(instructorId);
	                if (instructor != null) {
	                    if (scheduleService.isInstructorOccupied(instructor, timeStart, timeEnd,scheduleService.getScheduleById(scheduleId))) {
	                    	String instructorError="The selected instructor is already occupied at this time.";
	                    	model.addAttribute("instructorError", instructorError);
	                        return "redirect:/gym/modifyClass?classId=" + classId +"&gymId="+gymId+ "&scheduleId=" + scheduleId+"&instructorError="+instructorError;
	                    }
	                    classOfSchedule.setInstructor(instructor);
	                }
	            }
	            if (roomId.equals(classOfSchedule.getRoom().getRoomId())) {
	                Room room =gymService.getRoomById(roomId);
	                if (room != null) {
	                    if (scheduleService.isRoomOccupied(room, timeStart, timeEnd,scheduleService.getScheduleById(scheduleId))) {
	                    	String roomError= "The selected room is already occupied at this time.";
	                        model.addAttribute("roomError",roomError);
	                        return "redirect:/gym/modifyClass?classId=" + classId +"&gymId="+gymId+ "&scheduleId=" + scheduleId+"&roomError="+roomError;
	                    }
	                    classOfSchedule.setRoom(room);
	                }
	            }
	            classOfSchedule.setTime_start(timeStart);
	            classOfSchedule.setTime_end(timeEnd);
	            Workout workout = workoutService.findById(workoutId);
	            if (workout != null) {
	                classOfSchedule.setWorkout(workout);
	            }
	            classOfScheduleService.save(classOfSchedule);
	            for (Athletes participant : classOfSchedule.getParticipants()) {
	                String emailContent = "Class modification notification:\n\n"
	                        + "Class details after modification:\n"  + classOfSchedule.getTime_start()+" - "+classOfSchedule.getTime_end()+" \nWorkout: "+classOfSchedule.getWorkout().getName()+" \n Instructor:" + classOfSchedule.getInstructor().getInstructor_name()+"\nRoom: "+classOfSchedule.getRoom().getRoomName()+"\n\n";
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
	   classOcc.setSchedule(scheduleService.getScheduleById(scheduleId));
	   classOcc.setClassOfSchedule(classOfScheduleService.getClassOfScheduleById(classId));
	   classOcc.setCanceled(true);
	   classOfScheduleService.saveClassOccurrence(classOcc);

	     return "redirect:" + referringPageUrl;
	 }

	 @PostMapping("/uncancelClassOccurrence")
	 public String uncancelClassOccurrence(@RequestParam Long classId, @RequestParam("scheduleId") Long scheduleId,@RequestParam(value="classOccurrenceId",required=false) Long classOccurrenceId,HttpServletRequest request) {
		 String referringPageUrl = request.getHeader("referer");
		 ClassOccurrence classOccurrence = classOfScheduleService.getClassOccurrence(scheduleId, classId);
		 List<ClassOccurrence> occurrences=classOfScheduleService.findAll();
		 ClassOfSchedule classOfSchedule = classOfScheduleService.getClassOfScheduleById(classId);
	        if (classOccurrence != null) {
	        	occurrences.remove(classOccurrence);
	        	classOfScheduleService.deleteOccurrence(classOccurrence);
	            classOfSchedule.setIs_canceled(false);
	            classOfScheduleService.save(classOfSchedule);
	        }
	        return "redirect:" + referringPageUrl;
	  }

	 @PostMapping("/joinWaitingList")
	 @Transactional
	 public String joinWaitingList(@RequestParam("classOfScheduleId") Long classOfScheduleId, Authentication authentication, Model model, HttpServletRequest request) {
		Athletes a=athleteService.getAthlete(authentication.getName());
		ClassOfSchedule classOfSchedule=classOfScheduleService.getClassOfScheduleById(classOfScheduleId);
		if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ATHLETE"))) {
			if (classOfSchedule != null) {
		        WaitingListEntry entry = new WaitingListEntry();
		        entry.setClassOfSchedule(classOfSchedule);
		        entry.setAthlete(a);
		        entry.setJoinedAt(LocalDateTime.now());
		        classOfScheduleService.saveWaitingListEntry(entry);
		        model.addAttribute("isInWaitingList", true);
		    }
		}

	    String previousPageUrl = request.getHeader("Referer");
	    return "redirect:" + previousPageUrl;
	 }

}
