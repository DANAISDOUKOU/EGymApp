package dipl.danai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.ClassRating;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.ClassOfScheduleService;
import dipl.danai.app.service.ClassRatingService;
import dipl.danai.app.service.GymService;
@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class AthleteControllerTest {
	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private AthleteService athleteService;
	@MockBean
	private GymService gymService;
	@MockBean
	private ClassOfScheduleService classOfScheduleService;
	@MockBean
	private ClassRatingService classRatingService;

	 @Test
	    @WithMockUser(username = "example@gmail.com", authorities = "ATHLETE")
	    public void testAthletePage() throws Exception {
	        Athletes mockAthlete = new Athletes(356L, "kati", "kati", "example@gmail.com", "kapou", "Ioannina", "0123456789", null);
	        when(athleteService.getAthlete("example@gmail.com")).thenReturn(mockAthlete);
	        List<Gym> mockGyms = new ArrayList();
	        when(gymService.getGyms()).thenReturn(mockGyms);
	        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/athlete/dashboard"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("athlete/dashboard"))
	                .andReturn();
	        Object gymsAttribute = result.getModelAndView().getModel().get("gyms");

	    }

	 @Test
	    @WithMockUser(username="example@gmail.com",authorities = "ATHLETE")
	    public void testSeeGymsThatIamMember() throws Exception {
	    	 Athletes mockAthlete = new Athletes();
	    	 Set<Gym> mockGyms = new HashSet<>();
	    	 Gym gym = new Gym();
	    	 byte[] pictureBytes = new byte[] {};
	    	 gym.setPicture(pictureBytes);
	    	 mockGyms.add(gym);
	    	 mockAthlete.setGyms(mockGyms);
	         when(athleteService.getAthlete("example@gmail.com")).thenReturn(mockAthlete);
	        mockMvc.perform(MockMvcRequestBuilders.get("/athlete/GymsThatIAmMember"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("athlete/gymsThatIAmMember"));
	    } 	
	    @Test
	    @WithMockUser(authorities = "ATHLETE")
	    public void testViewAttendanceHistory() throws Exception {
	        Gym mockGym = new Gym();
	        mockGym.setGym_name("Example Gym"); 

	        when(gymService.getGymById(anyLong())).thenReturn(mockGym);

	        mockMvc.perform(MockMvcRequestBuilders.get("/athlete/history")
	                .param("gymId", "322"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("athlete/history"));
	    }

	    @Test
	    @WithMockUser(authorities = "ATHLETE")
	    public void testRateClass() throws Exception {
	        Athletes athlete = new Athletes();
	        athleteService.save(athlete);

	        when(athleteService.getAthlete(anyString())).thenReturn(athlete);
	        when(gymService.getGymById(anyLong())).thenReturn(new Gym());
	        when(classOfScheduleService.getClassOfScheduleById(anyLong())).thenReturn(new ClassOfSchedule());

	        mockMvc.perform(MockMvcRequestBuilders.post("/rate-class")
	                .param("classOfScheduleId", "383")
	                .param("gymId", "322")
	                .param("classRating", "5.0"))
	                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
	                .andExpect(MockMvcResultMatchers.redirectedUrl("/athlete/history"));

	        verify(athleteService, times(1)).getAthlete(anyString());
	        verify(gymService, times(1)).getGymById(anyLong());
	        verify(classOfScheduleService, times(1)).getClassOfScheduleById(anyLong());
	        verify(classRatingService, times(1)).saveClassRating(any(ClassRating.class));
	    }
}