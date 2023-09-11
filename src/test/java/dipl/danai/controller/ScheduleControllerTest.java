package dipl.danai.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOccurrence;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
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
	    	mockMvc.perform(MockMvcRequestBuilders.get("/gym/addClass")
	    	        .param("programId", programId)) 
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

	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("callAddClass", previousUrl);

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
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testGetProgram() throws Exception {
	        Schedule schedule = Mockito.mock(Schedule.class);
	        Gym gym = Mockito.mock(Gym.class);
	        String email = "testuser";
	        Authentication authentication = mock(Authentication.class);
	        when(authentication.getName()).thenReturn(email);
	        String str="2023-08-30";
	        Date date=Date.valueOf(str);
	        when(gymService.getGymByEmail("testuser")).thenReturn(gym);
	        when(gym.getGymSchedules()).thenReturn(new ArrayList<>());
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/createProgram")
	                .flashAttr("schedule", schedule)
	                .param("date", str))
	        		.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	        		 .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/createProgram"));
	        verify(gymService, times(1)).getGymByEmail(email);
	        verify(scheduleService, times(1)).saveSchedule(schedule);
	        verify(gym, times(1)).getGymSchedules();
	        verify(gymService, times(1)).saveGym(gym);
	    }
	    

	    @Test
	    @WithMockUser(username = "testuser", authorities = "ATLHETE")
	    public void testParticipateInClass() throws Exception {
	    	String expectedRedirectUrl = "http://localhost:8080/gym/workoutDetails";

	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("Referer", expectedRedirectUrl);
	        Athletes athlete = new Athletes();
	        athlete.setEmail("testuser");

	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        classOfSchedule.setClassOfScheduleId(1L);

	        when(athleteService.getAthlete("testuser")).thenReturn(athlete);
	        when(classOfScheduleService.getClassOfScheduleById(1L)).thenReturn(classOfSchedule);

	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/participate")
	                .param("classOfScheduleId", "1")
	        		.header("Referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl)); 
	    }

	    @Test
	    @WithMockUser(username = "testuser", authorities = "ATLHETE")
	    public void testCancelPosition() throws Exception {
	    	String expectedRedirectUrl = "http://localhost:8080/gym/workoutDetails";

	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("Referer", expectedRedirectUrl);
	        Athletes athlete = new Athletes();
	        athlete.setEmail("testuser");
	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        classOfSchedule.setClassOfScheduleId(1L);
	        when(athleteService.getAthlete("testuser")).thenReturn(athlete);
	        when(classOfScheduleService.getClassOfScheduleById(1L)).thenReturn(classOfSchedule);

	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/cancelPosition")
	                .param("classOfScheduleId", "1")
	                .header("Referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl)); 
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testGetClassScheduleDetails() throws Exception {
	        Long classOfScheduleId = 1L;
	        Long gymId = 123L;
	        Workout w=new Workout();
	        w.setName("yoga");
	        Instructor i=new Instructor();
	        i.setInstructor_name("example");
	        Room r=new Room();
	        r.setRoomName("1");
	        ClassOfSchedule classSchedule = new ClassOfSchedule();
	        classSchedule.setClassOfScheduleId(classOfScheduleId);
	        classSchedule.setWorkout(w);
	        classSchedule.setInstructor(i);
	        classSchedule.setRoom(r);
	        Gym gym=new Gym();
	        gym.setEmail("testuser");
	        gym.setGym_id(gymId);
	        when(classOfScheduleService.getClassScheduleDetails(classOfScheduleId)).thenReturn(classSchedule);
	        when(gymService.getGymByEmail("testuser")).thenReturn(gym);

	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/class-schedule-details/{classOfScheduleId}", classOfScheduleId)
	                .param("gymId", gymId.toString()))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/classOfScheduleDetails"))
	                .andExpect(MockMvcResultMatchers.model().attributeExists("classSchedule", "gym", "availableAthletes"));
	    }

	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testAddParticipant() throws Exception {
	        Long classOfScheduleId = 1L;
	        Long gymId = 123L;
	        List<Long> selectedAthleteIds = Arrays.asList(1L, 2L); 
	        ClassOfSchedule classSchedule = new ClassOfSchedule();
	        classSchedule.setClassOfScheduleId(classOfScheduleId);
	        Gym gym=new Gym();
	        gym.setEmail("testuser");
	        gym.setGym_id(gymId);
	        Athletes ath1=new Athletes();
	        
	        Athletes ath2=new Athletes();
	        when(classOfScheduleService.getClassOfScheduleById(classOfScheduleId)).thenReturn(classSchedule);
	        when(athleteService.getById(1L)).thenReturn(ath1);
	        when(athleteService.getById(2L)).thenReturn(ath2);

	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/add-participant")
	                .param("classOfScheduleId", classOfScheduleId.toString())
	                .param("selectedAthletes", "1", "2") 
	                .param("gymId", gymId.toString()))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/class-schedule-details/" + classOfScheduleId + "?gymId=" + gymId));
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testRemoveParticipant() throws Exception {
	    	 Long classOfScheduleId = 1L;
		        Long gymId = 123L;
		        ClassOfSchedule classSchedule = new ClassOfSchedule();
		        classSchedule.setClassOfScheduleId(classOfScheduleId);
		        Gym gym=new Gym();
		        gym.setEmail("testuser");
		        gym.setGym_id(gymId);
		        Athletes ath1=new Athletes();
		        ath1.setAthlete_id(1L);
		        Athletes ath2=new Athletes();
		        ath2.setAthlete_id(2L);
		        when(classOfScheduleService.getClassScheduleDetails(classOfScheduleId)).thenReturn(classSchedule);
		        when(athleteService.getById(1L)).thenReturn(ath1);
		        when(athleteService.getById(2L)).thenReturn(ath2);
		        List<Athletes> participants=new ArrayList();
		        participants.add(ath2);
		        participants.add(ath1);
		        classSchedule.setParticipants(participants);
		        mockMvc.perform(MockMvcRequestBuilders.post("/gym/remove-participant")
	                .param("gymId", gymId.toString())
	                .param("classOfScheduleId", classOfScheduleId.toString())
	                .param("removedParticipants", "1", "2"))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl("/gym/class-schedule-details/" + classOfScheduleId + "?gymId=" + gymId));
		     List<Athletes> updatedParticipants = classSchedule.getParticipants();
		     assertEquals(0, updatedParticipants.size());
	        verify(classOfScheduleService, times(1)).save(classSchedule);
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testShowDeleteClassPage() throws Exception {
	        Long programId = 1L;
	        Schedule schedule = new Schedule();
	        schedule.setSchedule_id(programId);
	        when(scheduleService.getScheduleById(programId)).thenReturn(schedule);
	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/deleteClass")
	                .param("programId", programId.toString()))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/deleteClass"))
	                .andExpect(MockMvcResultMatchers.model().attribute("program", schedule));
	        verify(scheduleService, times(1)).getScheduleById(programId);
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testDeleteSelectedClasses() throws Exception {
	        Long programId = 1L;
	        List<Long> selectedClassIds = Arrays.asList(1L, 2L);
	        Schedule schedule = new Schedule();
	        schedule.setSchedule_id(programId);
	        List<ClassOfSchedule> workoutList = new ArrayList<>();
	        ClassOfSchedule c1=new ClassOfSchedule();
	        c1.setClassOfScheduleId(1L);
	        ClassOfSchedule c2=new ClassOfSchedule();
	        c2.setClassOfScheduleId(2L);
	        ClassOfSchedule c3=new ClassOfSchedule();
	        c3.setClassOfScheduleId(3L);
	        workoutList.add(c1);
	        workoutList.add(c2);
	        workoutList.add(c3);
	        schedule.setScheduleClasses(workoutList);
	        when(scheduleService.getScheduleById(programId)).thenReturn(schedule);
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/deleteClass")
	                .param("programId", programId.toString())
	                .param("selectedClasses", "1", "2"))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

	        List<ClassOfSchedule> updatedWorkoutList = schedule.getScheduleClasses();
	        assertEquals(1,updatedWorkoutList.size());
	        verify(scheduleService, times(1)).saveUpdatedSchedule(schedule);
	    }
	    
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testModifyClass() throws Exception {
	        Long classId = 1L;
	        Long gymId = 123L;
	        Long programId=1L;
	        Workout w= new Workout();
	        w.setName("yoga");
	        Instructor i= new Instructor();
	        i.setInstructor_name("example");
	        Room r= new Room();
	        r.setRoomName("1");
	        Gym gym= Mockito.mock(Gym.class);
	        gym.setGym_id(gymId);
	        String str="2023-08-30";
	        Date date=Date.valueOf(str);
	        Schedule schedule =  Mockito.mock(Schedule.class);
	        schedule.setSchedule_id(programId);
	        schedule.setWork_out_date(date);
	        ClassOfSchedule classOfSchedule= new ClassOfSchedule();
	        classOfSchedule.setClassOfScheduleId(classId);
	        classOfSchedule.setWorkout(w);
	        classOfSchedule.setInstructor(i);
	        classOfSchedule.setRoom(r);
	        classOfSchedule.setIs_canceled(false);
	        ClassOccurrence c=new ClassOccurrence();
	        
	        when(classOfScheduleService.getClassOccurrence(programId, classId)).thenReturn(c);
	        when(gymService.getGymById(gymId)).thenReturn(gym);
	        when(scheduleService.getScheduleById(programId)).thenReturn(schedule);
	        when(classOfScheduleService.getClassOfScheduleById(classId)).thenReturn(classOfSchedule);
	        when(gym.getGymWorkouts()).thenReturn(new ArrayList<>());
	        when(gym.getGymInstructors()).thenReturn(new HashSet<>());
	        when(gym.getRooms()).thenReturn(new ArrayList<>());
	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/modifyClass")
	                .param("classId", classId.toString())
	                .param("gymId", gymId.toString())
	                .param("scheduleId", programId.toString()))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/modifyClass"))
	                .andExpect(MockMvcResultMatchers.model().attributeExists("classOfSchedule", "workouts", "instructors", "rooms", "classOccurrence", "schedule", "gymId"));
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testPerformModification() throws Exception {
	    	String expectedRedirectUrl = "http://localhost:8080/gym/modifyClass";
	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("referer", expectedRedirectUrl);
	        Long classId = 1L;
	        Long workoutId=1L;
	        Long instructorId=1L;
	        Long roomId=1L;
	        Long scheduleId=1L;
	        Long gymId=1L;
	        String time1="19:00:00";
	        String time2="20:00:00";
	        Time startTime=Time.valueOf(time1);
	        Time endTime=Time.valueOf(time2);
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/performModification")
	                .param("classId", classId.toString())
	                .param("timeStart", startTime.toString())
	                .param("timeEnd", endTime.toString())
	                .param("instructorId", instructorId.toString())
	                .param("roomId", roomId.toString())
	                .param("scheduleId", scheduleId.toString())
	                .param("gymId", gymId.toString())
	                .param("workoutId", workoutId.toString())
	                .header("referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl)); 
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testCancelClassOccurrence() throws Exception {
	        Long classOccurrenceId = 1L;
	        Long scheduleId = 2L;
	        Long classId = 3L;
	        String expectedRedirectUrl = "http://localhost:8080/gym/modifyClass";
	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("referer", expectedRedirectUrl);
	        
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/cancelClassOccurrence")
	                .param("classOccurrenceId", classOccurrenceId.toString())
	                .param("scheduleId", scheduleId.toString())
	                .param("classId", classId.toString())
	                .header("referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl));
	        
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
	    public void testUncancelClassOccurrence() throws Exception {
	        Long classId = 1L;
	        Long scheduleId = 2L;
	        Long classOccurrenceId = 3L;
	        String expectedRedirectUrl = "http://localhost:8080/gym/modifyClass";
	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("referer", expectedRedirectUrl);
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/uncancelClassOccurrence")
	                .param("classId", classId.toString())
	                .param("scheduleId", scheduleId.toString())
	                .param("classOccurrenceId", classOccurrenceId.toString())
	                .header("referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl));
	        
	    }
	    
	    @Test
	    @WithMockUser(username = "testuser", authorities = "ATHLETE")
	    public void testJoinWaitingList() throws Exception {
	        Long classOfScheduleId = 1L;
	        
	        String expectedRedirectUrl = "http://localhost:8080/gym/workoutDetails";
	        MockHttpSession session = new MockHttpSession();
	        session.setAttribute("Referer", expectedRedirectUrl); 
	        
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/joinWaitingList")
	                .param("classOfScheduleId", classOfScheduleId.toString())
	                .header("Referer", expectedRedirectUrl))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl(expectedRedirectUrl));
	        
	    }
}