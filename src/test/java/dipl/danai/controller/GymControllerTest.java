package dipl.danai.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Payment;
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

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class GymControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
    private GymService gymService;

	@MockBean
    private AthleteService athleteService;

	@MockBean
	private MembershipService membershipService;

	@MockBean
	private WorkoutService workoutService;

	@MockBean
	private PaymentService paymentService;

	@MockBean
	private InstructorService instructorService;

	@MockBean
	private GymRatingService gymRatingService;

	@MockBean
	private ClassOfScheduleService classOfScheduleService;

	@MockBean
	private ScheduleService scheduleService;

	 @Test
	    @WithMockUser(username = "testuser")
	    public void testGymPage() throws Exception {
	        String email = "testuser";
	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(email);

	        Gym gym = Mockito.mock(Gym.class); 
	        when(gymService.getGymByEmail(email)).thenReturn(gym);
	        List<Room> rooms = new ArrayList<>(); 
	        when(gym.getRooms()).thenReturn(rooms); 
	        LocalDate currentDate = LocalDate.now();
	        Schedule schedule = new Schedule(); 
	        when(scheduleService.getScheduleByDateAndGym(Date.valueOf(currentDate), gym)).thenReturn(schedule);

	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/dashboard"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/dashboard"))
	                .andReturn();
	    }
	 

	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testGymRoom() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setSession(new MockHttpSession());

	    Model model = Mockito.mock(Model.class);

	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/createRoom"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("/gym/createRoom"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("room"));
	}


	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testCreateGymRoom() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setSession(new MockHttpSession());

		Gym gym= Mockito.mock(Gym.class);
		when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	    mockMvc.perform(MockMvcRequestBuilders.post("/gym/createRoom")
	            .param("roomAttribute", "roomValue"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("/gym/createRoom"));

	}

	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testGymFitnessProgram() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setSession(new MockHttpSession());
	    Gym mockedGym =Mockito.mock(Gym.class);
	    Mockito.when(gymService.getGymByEmail("testuser")).thenReturn(mockedGym);
	    Mockito.when(mockedGym.getGymSchedules()).thenReturn(new ArrayList());
	    Mockito.when(mockedGym.getRooms()).thenReturn(new ArrayList());

	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/program"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/program"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("rooms", "gym", "programList", "workoutList"));

	}

	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testUpdateFitnessProgram() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setSession(new MockHttpSession());
	    Model model = Mockito.mock(Model.class);
	    Gym mockedGym = Mockito.mock(Gym.class);
	    
	    Long scheduleId = 123L; 
	    LocalDate date = LocalDate.now();
	    
	    Schedule mockedSchedule = new Schedule();
	    mockedSchedule.setSchedule_id(scheduleId);
	    mockedSchedule.setWork_out_date(Date.valueOf(date));
	    
	    Mockito.when(gymService.getGymByEmail(ArgumentMatchers.anyString())).thenReturn(mockedGym);
	    Mockito.when(mockedGym.getGymSchedules()).thenReturn(new ArrayList());
	    Mockito.when(scheduleService.getScheduleById(scheduleId)).thenReturn(mockedSchedule); // Stubbing the service call
	    
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/updateProgram")
	            .param("programId", String.valueOf(scheduleId))) // Pass scheduleId as a query parameter
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/updateProgram"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "programList", "workoutList"));
	}


/*	@Test
	@WithMockUser(username = "testuser", authorities = "ATHLETE")
	public void testViewGymDetails() throws Exception {
	    Athletes a = new Athletes();
	    Schedule schedule = new Schedule();
	    schedule.setWork_out_date(Date.valueOf("2023-10-03"));
	    ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	    Gym mockedGym = new Gym();
	    mockedGym.setGymSchedules(Collections.singletonList(schedule));

	    Mockito.when(gymService.getGymById(ArgumentMatchers.anyLong())).thenReturn(mockedGym);
	    Mockito.when(gymService.checkIfAlreadyMember(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
	    Mockito.when(gymService.checkIfHasSpecificMembership(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(true))).thenReturn(false);
	    Mockito.when(gymService.findExistingMembership(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(null);

	    MembershipType existingMembership = null;
	    Mockito.when(gymService.findExistingMembership(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(existingMembership);

	    Mockito.when(athleteService.getAthlete("testuser")).thenReturn(a);

	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/{id}", 1L)
	            .with(SecurityMockMvcRequestPostProcessors.user("testuser")))// Add user and authorities here
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/details"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "alreadyMember", "hasAlreadyMembershipType", "memberships", "programList", "workoutList"));
	}*/


	@Test
	@WithMockUser(username = "testuser", authorities = "ATHLETE")
	public void testSearchGyms() throws Exception {
		 Athletes a=new Athletes();
		 Mockito.when(athleteService.getAthlete("testuser")).thenReturn(a);
		 Mockito.when(gymService.searchGyms(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(new ArrayList());
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/search")
	            .param("searchBy", "searchField")
	            .param("query", "searchQuery")
	            .param("sortBy", "name"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/list"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("gyms"))
	            .andReturn();
	}

	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testGetMembers() throws Exception {
		Gym gym=new Gym();
		gym.setGym_id(1L);
	    Mockito.when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	    Mockito.when (gymService.findMembers(gym.getGym_id())).thenReturn(new HashSet());
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/seeMembers"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/seeMembers"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("members"))
	            .andReturn();
	}

	@Test
	@WithMockUser(username = "testuser", authorities = "GYM")
	public void testMembershipTypes() throws Exception {
		Gym gym=Mockito.mock(Gym.class);
		gym.setGym_id(1L);
	    Mockito.when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	    Mockito.when (gym.getGym_memberships()).thenReturn(new ArrayList());
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/membershipTypes"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/membershipTypes"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("membershipList"))
	            .andReturn();
	}

	@Test
	@WithMockUser(username = "testuser", authorities = "ATHLETE")
	public void testSubscribeToGym() throws Exception {
		 Long gymId = 1L;
		 String expectedRedirectUrl = "http://localhost:8080/gym/"+gymId;
	     MockHttpSession session = new MockHttpSession();
	     session.setAttribute("referer", expectedRedirectUrl);
		 Athletes a=new Athletes();
		 Mockito.when(athleteService.getAthlete("testuser")).thenReturn(a);
		 Gym gym=Mockito.mock(Gym.class);
	    gym.setGym_id(gymId);
	    Mockito.when(gymService.getGymById(gymId)).thenReturn(gym);
	    Mockito.when(gym.getGymMembers()).thenReturn(new HashSet());
	    mockMvc.perform(MockMvcRequestBuilders.post("/gym/subscribe")
	            .param("gymId", String.valueOf(gymId))
	            .header("referer", expectedRedirectUrl))
	            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	            .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl))
	            .andReturn();
	}

	 @Test
	 @WithMockUser(username = "testuser", authorities = "ATHLETE")
	 public void testSelectMembership() throws Exception {
	     Long membershipId = 1L;
	     Long gymId = 123L;
	     double newAmount = 50.0;
		 String expectedRedirectUrl = "http://localhost:8080/gym/details";
		 MockHttpSession session = new MockHttpSession();
	     session.setAttribute("referer", expectedRedirectUrl);
	     MembershipType membershipType = new MembershipType();
	     Athletes athlete = new Athletes();
	     Gym gym = new Gym();
	     Payment payment = new Payment();
	     when(membershipService.findMembershiById(membershipId)).thenReturn(membershipType);
	     when(athleteService.getAthlete("testuser")).thenReturn(athlete);
	     when(gymService.getGymById(gymId)).thenReturn(gym);
	     doNothing().when(paymentService).makePayment(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyDouble());
	      mockMvc.perform(MockMvcRequestBuilders.post("/gym/selectMembership")
	                .param("membershipId", membershipId.toString())
	                .param("gymId", gymId.toString())
	                .param("new_amount", Double.toString(newAmount))
	                .header("referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl));
	 }

	 @Test
	 @WithMockUser(username = "testuser", authorities = "GYM")
	 public void testTypeOfWorkouts() throws Exception {
	   String email = "testuser";
	   Gym gym =Mockito.mock(Gym.class);
	   List<Workout> workouts = new ArrayList<>();
	   when(gymService.getGymByEmail(email)).thenReturn(gym);
	   when(workoutService.findAll()).thenReturn(workouts);
	   when(gym.getGymWorkouts()).thenReturn(new ArrayList());
	   mockMvc.perform(MockMvcRequestBuilders.get("/gym/typeOfWorkouts"))
	          .andExpect(MockMvcResultMatchers.status().isOk())
	          .andExpect(MockMvcResultMatchers.view().name("gym/typeOfWorkouts"))
	          .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "workoutGyms"));
	 }

	 @Test
	 @WithMockUser(username = "testuser", authorities = "GYM")
	 public void testAddWorkoutsToGym() throws Exception {
	     Long workoutId = 1L;
	     Gym gym = Mockito.mock(Gym.class);
	     Workout workout = new Workout();
	     when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	     when(workoutService.findById(workoutId)).thenReturn(workout);
	     when(gym.getGymWorkouts()).thenReturn(new ArrayList());
	     mockMvc.perform(MockMvcRequestBuilders.post("/gym/add-workout") // Corrected endpoint URL
	            .param("selectedWorkout", workoutId.toString())) // Parameter name should match your controller
	            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	            .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/addWorkouts"));
	     ArgumentCaptor<Gym> gymCaptor = ArgumentCaptor.forClass(Gym.class);
	     verify(gymService, times(1)).saveUpdatedGym(gymCaptor.capture());
	     assertEquals(gym, gymCaptor.getValue());
	 }

	  @Test
	  @WithMockUser(username = "testuser", authorities = "GYM")
	  public void testInstructorsGym() throws Exception {
	     Gym gym = new Gym();
	     List<Instructor> instructors = Arrays.asList(new Instructor());
	     when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	     when(instructorService.findAll()).thenReturn(instructors);
	     mockMvc.perform(MockMvcRequestBuilders.get("/gym/MeetTheInstructors"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/MeetTheInstructors"))
	            .andExpect(MockMvcResultMatchers.model().attribute("instructorsGyms", gym.getGymInstructors()))
	            .andExpect(MockMvcResultMatchers.model().attribute("instructors", instructors));
	  }

	  @Test
	  @WithMockUser(username = "testuser", authorities = "GYM")
	  public void testAddInstructorToGym() throws Exception {
	     Long instructorId = 1L;
	     Gym gym =Mockito.mock(Gym.class);
	     Instructor instructor = new Instructor();
	     when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	     when(instructorService.getById(instructorId)).thenReturn(instructor);
	     when(gym.getGymInstructors()).thenReturn(new HashSet());
	     mockMvc.perform(MockMvcRequestBuilders.post("/addInstructorToGym")
	    	        .param("selectedInstructor", instructorId.toString()))
	    	        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	    	        .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/addInstructor"));

	     ArgumentCaptor<Gym> gymCaptor = ArgumentCaptor.forClass(Gym.class);
	     verify(gymService, times(1)).saveGym(gymCaptor.capture());
	     assertEquals(gym, gymCaptor.getValue());
	  }

	  @Test
	  @WithMockUser(username = "testuser", authorities = "GYM")
	  public void testRemoveInstructorFromGym() throws Exception {
	      Long instructorId = 1L;
	      Gym gym = Mockito.mock(Gym.class);
	      gym.setGymInstructors(new HashSet<>());
	      
	      Instructor instructor = new Instructor();
	      gym.getGymInstructors().add(instructor);

	      when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	      when(instructorService.getById(instructorId)).thenReturn(instructor);
	      mockMvc.perform(MockMvcRequestBuilders.get("/removeInstructorFromGym") // Use POST method
	              .param("instructorId", instructorId.toString())) // Change "selectedInstructors" to "instructorId"
	              .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	              .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/MeetTheInstructors?gymId=0"));

	      verify(gymService, times(1)).saveGym(gym);
	  }


	  @Test
	  @WithMockUser(username = "testuser", authorities = "SOME_AUTHORITY")
	  public void testViewGymDetailsInstructor() throws Exception {
	      Long gymId = 1L;
	      Long instructorId = 2L;
	      Gym gym = new Gym();
	      Instructor instructor = new Instructor();
	      instructor.setInstructor_id(instructorId);
	      Schedule schedule = new Schedule();
	      List<Schedule> schedules = new ArrayList<>();
	      gym.setGymSchedules(schedules);
	      ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	      Workout w=new Workout();
	      w.setName("yoga");
	      classOfSchedule.setWorkout(w);
	      LocalDate workOutDate = LocalDate.now();
	      schedule.setWork_out_date(Date.valueOf(workOutDate));
	      classOfSchedule.setInstructor(instructor);
	      schedule.getScheduleClasses().add(classOfSchedule);
	      gym.getGymSchedules().add(schedule);
	      when(gymService.getGymById(gymId)).thenReturn(gym);
	      when(instructorService.getByEmail("testuser")).thenReturn(instructor);
	      
	      mockMvc.perform(MockMvcRequestBuilders.get("/gym/instructor" + gymId))
	          .andExpect(MockMvcResultMatchers.status().isOk())
	          .andExpect(MockMvcResultMatchers.view().name("gym/instructor-classes-details"))
	          .andExpect(MockMvcResultMatchers.model().attribute("gym", gym))
	          .andExpect(MockMvcResultMatchers.model().attribute("gymId", gymId))
	          .andExpect(MockMvcResultMatchers.model().attribute("instructorId", instructorId))
	          .andExpect(MockMvcResultMatchers.model().attributeExists("scheduleClassesMap"));
	  }


    @Test
    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
    public void testViewGymInstructorDetails() throws Exception {
        Long instructorId = 1L;
        Long gymId = 2L;
        Gym gym = new Gym();
        gym.setGym_id(gymId);
        List<ClassOfSchedule> classes = new ArrayList<>();
        when(instructorService.getClassesByGym(instructorId, gymId)).thenReturn(classes);
        mockMvc.perform(MockMvcRequestBuilders.get("/gym/instructor-classes-details")
                .param("gymId", gymId.toString())
                .param("instructorId", instructorId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/instructor-classes-details"))
                .andExpect(MockMvcResultMatchers.model().attribute("classes", classes));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "ATHLETE")
    public void testRateGym() throws Exception {
        Long gymId = 1L;
        int rating = 5;
        Gym gym = new Gym();
        Athletes athlete = new Athletes();
        when(gymService.getGymById(gymId)).thenReturn(gym);
        when(athleteService.getAthlete("testuser")).thenReturn(athlete);
        when(gymRatingService.calculateAverageRating(gym)).thenReturn(4.5);
        mockMvc.perform(MockMvcRequestBuilders.post("/rate-gym/{gymId}", gymId)
                .param("rating", String.valueOf(rating)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/" + gymId + "?"));

        verify(gymRatingService, times(1)).addRating(gym, athlete, rating);
        verify(gymService, times(1)).saveUpdatedGym(gym);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "ATHLETE")
    public void testShowWorkoutDetails() throws Exception {
        Long workoutId = 1L;
        Long classOfScheduleId = 2L;
        Long scheduleId = 3L;
        Long gymId = 4L;
        Workout workout = new Workout();
        workout.setWorkoutId(workoutId);
        workout.setName("yoga");
        Athletes athlete = new Athletes();
        
        // Mock the behavior of your Schedule object
        Schedule schedule = new Schedule();
        schedule.setSchedule_id(scheduleId);
        schedule.setWeeks(1);
        
        ClassOfSchedule classOfSchedule = Mockito.mock(ClassOfSchedule.class);
        Gym gym = mock(Gym.class);
        when(gym.getGym_id()).thenReturn(4L);
        // Mock the behavior of your services
        when(workoutService.findById(workoutId)).thenReturn(workout);
        when(athleteService.getAthlete("testuser")).thenReturn(athlete);
        when(classOfScheduleService.getClassOfScheduleById(classOfScheduleId)).thenReturn(classOfSchedule);
        when(classOfSchedule.getParticipants()).thenReturn(new ArrayList<>());
        when(classOfSchedule.getWorkout()).thenReturn(workout);
        when(scheduleService.getScheduleById(scheduleId)).thenReturn(schedule);
        when(gymService.getGymById(anyLong())).thenReturn(gym); // Mocking gymService behavior

        mockMvc.perform(MockMvcRequestBuilders.get("/workout/{workoutId}/class/{classOfScheduleId}/schedule/{scheduleId}/gym/{gymId}",
                workoutId, classOfScheduleId, schedule.getSchedule_id(), gymId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/workoutDetails"))
                .andExpect(MockMvcResultMatchers.model().attribute("workout", workout))
                .andExpect(MockMvcResultMatchers.model().attribute("classOfSchedule", classOfSchedule))
                .andExpect(MockMvcResultMatchers.model().attributeExists("position", "isInWaitingList", "alreadySelected", "allPositionsReserved"));
    }


    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testShowDeleteProgramPage() throws Exception {
        Gym gym = new Gym(); // Create a Gym object with required data
        Schedule schedule = new Schedule(); // Create a Schedule object with required data

        Mockito.when(gymService.getGymByEmail(Mockito.anyString())).thenReturn(gym);
        Mockito.when(scheduleService.getScheduleById(Mockito.anyLong())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.get("/gym/deleteProgram")
                .param("programId", "1") // Adjust the programId value as needed
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/deleteProgram"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "deletableSchedule"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testDeleteSchedule() throws Exception {
        Gym gym = Mockito.mock(Gym.class);
        Mockito.when(gymService.getGymByEmail(ArgumentMatchers.anyString())).thenReturn(gym);
        gym.setGymSchedules(new ArrayList());
        Schedule schedule = new Schedule();
        gym.getGymSchedules().add(schedule);
        Mockito.when(scheduleService.getScheduleById(ArgumentMatchers.anyLong())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.post("/gym/deleteSchedule")
                .param("scheduleId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/deleteProgram"));
    }

    @Test
    public void testWatchDetailsOfGym() throws Exception {
    	Long gymId=1L;
        Gym gym = new Gym();
        Mockito.when(gymService.getGymById(ArgumentMatchers.anyLong())).thenReturn(gym);
        gym.setGym_id(gymId);
        gym.setGymSchedules(new ArrayList());
        Schedule schedule = new Schedule();
        Mockito.when(scheduleService.getScheduleById(ArgumentMatchers.anyLong())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.get("/visitor/"+gymId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("visitor/gym-details"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "programList", "workoutList"));
    }


}
