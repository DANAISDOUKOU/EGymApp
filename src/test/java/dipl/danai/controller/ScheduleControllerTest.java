package dipl.danai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.controller.ScheduleController;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Workout;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.InstructorService;
import dipl.danai.app.service.ScheduleService;
import dipl.danai.app.service.WorkoutService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class ScheduleControllerTest {
	 @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private ScheduleService scheduleService;

	    @MockBean
	    private InstructorService instructorService;

	    @MockBean
	    private GymService gymService;

	    @MockBean
	    private WorkoutService workoutService;

	    @MockBean
	    private AthleteService athleteService;
	    
	    @MockBean
	    private ClassOfScheduleService classOfScheduleService;
	    
	    
	   
	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testAddClass() throws Exception {
	    	Gym gym= Mockito.mock(Gym.class);
	    	List<Workout> workouts = new ArrayList<>();
	    	String programId = "384";
	    	when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	    	//when(gym.getGymWorkouts()).thenReturn(workouts);
	    	mockMvc.perform(MockMvcRequestBuilders.get("/gym/addClass")
	    	        .param("programId", programId)) // Set 'programId' as a request parameter
	    	        .andExpect(MockMvcResultMatchers.status().isOk())
	    	        .andExpect(MockMvcResultMatchers.view().name("gym/addClass"))
	    	        .andExpect(MockMvcResultMatchers.model().attributeExists("workouts", "instructors", "rooms", "programId"));
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testAddClassPost() throws Exception {
	    	
	    	String previousUrl = "http://localhost:8080/gym/createProgram";
	        String email = "testuser";
	        String roomName = "Room1";
	        String workoutName = "Workout1";
	        String instructorName = "Instructor1";
	        String startTime = "10:00";
	        String endTime = "11:00";
	        int capacity = 20;
	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(email);
	        // Mock necessary services
	        Gym gym = Mockito.mock(Gym.class); 
	        gym.setGym_id(1L);
	        when(gymService.getGymByEmail("testuser")).thenReturn(gym);

	        Workout workout = Mockito.mock(Workout.class); 
	        workout.setName(workoutName);
	        when(workoutService.getByName(workoutName)).thenReturn(workout);

	        Instructor instructor = Mockito.mock(Instructor.class); 
	        instructor.setInstructor_name(instructorName);
	        when(instructorService.getByName(instructorName)).thenReturn(instructor);

	        Room room = Mockito.mock(Room.class); 
	        room.setRoomName(roomName);
	        room.setGym(gym);
	        when(gymService.getRoomByName(roomName, gym.getGym_id())).thenReturn(room);

	        // Create a HttpSession
	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("callAddClass", previousUrl);

	        // Perform the POST request
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/addClass")
	                .param("workouts", workoutName)
	                .param("instructors", instructorName)
	                .param("start_time", startTime)
	                .param("end_time", endTime)
	                .param("rooms", roomName)
	                .param("capacity", String.valueOf(capacity))
	                .session(session)
	        )
	        		.andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	                .andExpect(MockMvcResultMatchers.redirectedUrl(previousUrl));
	        
	      	verify(gymService, times(1)).getGymByEmail(email);
	        verify(workoutService, times(1)).getByName(workoutName);
	        verify(instructorService, times(2)).getByName(instructorName);
	        verify(gymService, times(1)).getRoomByName(roomName, gym.getGym_id());
	        verify(classOfScheduleService, times(1)).save(any(ClassOfSchedule.class));
	        verify(scheduleService, times(1)).saveClass(any(ClassOfSchedule.class));
	        verify(instructorService, times(1)).saveClass(any(ClassOfSchedule.class));
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testCreateProgram() throws Exception {
	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/createProgram"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/createProgram"))
	                .andExpect(MockMvcResultMatchers.model().attributeExists("fitnessProgram"));
	    }
}
