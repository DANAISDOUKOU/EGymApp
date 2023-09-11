package dipl.danai.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
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
	@WithMockUser(username = "testuser", authorities = "GYM") 
	public void testGymPage() throws Exception {
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/dashboard"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/dashboard"));
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
	    Gym mockedGym =Mockito.mock(Gym.class);
	    Mockito.when(gymService.getGymByEmail(Mockito.anyString())).thenReturn(mockedGym);
	    Mockito.when(mockedGym.getGymSchedules()).thenReturn(new ArrayList());
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/updateProgram"))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/updateProgram"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "programList", "workoutList"));
	    
	}
	
	@Test
	@WithMockUser(username = "testuser", authorities = "ATHLETE") 
	public void testViewGymDetails() throws Exception {
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setSession(new MockHttpSession());
	    Gym mockedGym =Mockito.mock(Gym.class);
	    Athletes a=new Athletes();
	    Mockito.when(gymService.getGymById(Mockito.anyLong())).thenReturn(mockedGym);
	    Mockito.when(gymService.checkIfAlreadyMember(Mockito.anyLong(), Mockito.anyString())).thenReturn(true); 
	    Mockito.when(gymService.checkIfHasSpecificMembership(Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true))).thenReturn(false);	
	    MembershipType existingMembership =null;
	    Mockito.when(gymService.findExistingMembership(Mockito.anyLong(), Mockito.anyLong())).thenReturn(existingMembership);
	    Mockito.when(athleteService.getAthlete("testuser")).thenReturn(a);
	    mockMvc.perform(MockMvcRequestBuilders.get("/gym/{id}", 1L))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("gym/details"))
	            .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "alreadyMember", "hasAlreadyMembershipType", "memberships", "programList", "workoutList"));
	    
	}
	
	@Test
	@WithMockUser(username = "testuser", authorities = "ATHLETE") 
	public void testSearchGyms() throws Exception {
		 Athletes a=new Athletes();
		 Mockito.when(athleteService.getAthlete("testuser")).thenReturn(a);
		 Mockito.when(gymService.searchGyms(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(new ArrayList());
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
	     Double newAmount = 50.0; 
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
	     doNothing().when(paymentService).makePayment(Mockito.any(), Mockito.any(), Mockito.anyDouble());
	      mockMvc.perform(MockMvcRequestBuilders.post("/gym/selectMembership")
	                .param("membershipId", membershipId.toString())
	                .param("gymId", gymId.toString())
	                .param("new_amount", newAmount.toString())
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
	          .andExpect(MockMvcResultMatchers.model().attributeExists("workouts", "workoutGyms"));
	 }
	 
	 @Test
	 @WithMockUser(username = "testuser", authorities = "GYM")
	 public void testAddWorkoutsToGym() throws Exception {
	    Long workoutId = 1L;
	    List<Long> selectedWorkouts = Arrays.asList(workoutId);
	    Gym gym = Mockito.mock(Gym.class);
	    Workout workout = new Workout();
	    when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	    when(workoutService.findById(workoutId)).thenReturn(workout);
	    when(gym.getGymWorkouts()).thenReturn(new ArrayList());
	    mockMvc.perform(MockMvcRequestBuilders.post("/addWorkoutsToGym")
	           .param("selectedWorkouts", workoutId.toString()))
	           .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	           .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/typeOfWorkouts")); 
	     verify(gymService, times(1)).saveGym(gym);
	  }

	  @Test
	  @WithMockUser(username = "testuser", authorities = "SOME_AUTHORITY")
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
	  @WithMockUser(username = "testuser", authorities = "SOME_AUTHORITY")
	  public void testAddInstructorToGym() throws Exception {
	     Long instructorId = 1L;
	     List<Long> selectedInstructors = Arrays.asList(instructorId);
	     Gym gym =Mockito.mock(Gym.class);
	     Instructor instructor = new Instructor();
	     when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	     when(instructorService.getById(instructorId)).thenReturn(instructor);
	     when(gym.getGymInstructors()).thenReturn(new HashSet());
	     mockMvc.perform(MockMvcRequestBuilders.post("/addInstrucotrToGym")
	            .param("selectedInstructors", instructorId.toString()))
	            .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	            .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/MeetTheInstructors")); 
	     verify(gymService, times(1)).saveGym(gym);
	  }

	 @Test
	 @WithMockUser(username = "testuser", authorities = "GYM")
	 public void testRemoveInstructorFromGym() throws Exception {
	     Long instructorId = 1L;
	     List<Long> selectedInstructors = Arrays.asList(instructorId);
	     Gym gym = Mockito.mock(Gym.class);
	     Instructor instructor = new Instructor();
	     gym.getGymInstructors().add(instructor); 
	     when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	     when(instructorService.getById(instructorId)).thenReturn(instructor);
	      mockMvc.perform(MockMvcRequestBuilders.post("/removeInstructorFromGym")
	           .param("selectedInstructors", instructorId.toString()))
	           .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	           .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/MeetTheInstructors"));

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
	   List<Schedule> scheduless=new ArrayList();
	   gym.setGymSchedules(scheduless);
	   ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	   gym.getGymSchedules().add(schedule);
	   schedule.getScheduleClasses().add(classOfSchedule);
	   classOfSchedule.setInstructor(instructor);
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
        Workout workout = new Workout();
        workout.setWorkoutId(workoutId);
        workout.setName("yoga");
        Athletes athlete = new Athletes();
        ClassOfSchedule classOfSchedule = Mockito.mock(ClassOfSchedule.class);

        when(workoutService.findById(workoutId)).thenReturn(workout);
        when(athleteService.getAthlete("testuser")).thenReturn(athlete);
        when(classOfScheduleService.getClassOfScheduleById(classOfScheduleId)).thenReturn(classOfSchedule);
        when(classOfSchedule.getParticipants()).thenReturn(new ArrayList());
        when(classOfSchedule.getWorkout()).thenReturn(workout);
        mockMvc.perform(MockMvcRequestBuilders.get("/workout/{workoutId}/class/{classOfScheduleId}/schedule/{scheduleId}", workoutId, classOfScheduleId, scheduleId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/workoutDetails")) 
                .andExpect(MockMvcResultMatchers.model().attribute("workout", workout))
                .andExpect(MockMvcResultMatchers.model().attribute("classOfSchedule", classOfSchedule))
                .andExpect(MockMvcResultMatchers.model().attributeExists("position", "isInWaitingList", "alreadySelected", "allPositionsReserved"));
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testShowDeleteProgramPage() throws Exception {
        Gym gym = Mockito.mock(Gym.class);
        Mockito.when(gymService.getGymByEmail(Mockito.anyString())).thenReturn(gym);
        Mockito.when(gym.getGymSchedules()).thenReturn(new ArrayList());
        mockMvc.perform(MockMvcRequestBuilders.get("/gym/deleteProgram")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/deleteProgram"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("deletableSchedules"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testDeleteSchedule() throws Exception {
        Gym gym = Mockito.mock(Gym.class);
        Mockito.when(gymService.getGymByEmail(Mockito.anyString())).thenReturn(gym);
        gym.setGymSchedules(new ArrayList());
        Schedule schedule = new Schedule(); 
        gym.getGymSchedules().add(schedule);
        Mockito.when(scheduleService.getScheduleById(Mockito.anyLong())).thenReturn(schedule);

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
        Mockito.when(gymService.getGymById(Mockito.anyLong())).thenReturn(gym);
        gym.setGym_id(gymId);
        gym.setGymSchedules(new ArrayList());
        Schedule schedule = new Schedule(); 
        Mockito.when(scheduleService.getScheduleById(Mockito.anyLong())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.get("/visitor/"+gymId) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("visitor/gym-details"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("gym", "programList", "workoutList"));
    }


}
