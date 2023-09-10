package dipl.danai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.WorkoutService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class WorkoutControllerTest {
	 @Autowired
	 private MockMvc mockMvc;

	    @MockBean
	    private WorkoutService workoutService;

	    @MockBean
	    private GymService gymService;

	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testCreateClass() throws Exception {
	        mockMvc.perform(MockMvcRequestBuilders.get("/gym/createClass"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/createClass"))
	                .andExpect(MockMvcResultMatchers.model().attributeExists("workout"));
	    }

	    @Test
	    @WithMockUser(username = "testuser", authorities = "GYM")
	    public void testCreateClassWorkout() throws Exception {
	        mockMvc.perform(MockMvcRequestBuilders.post("/gym/createClass")
	                .param("name", "Yoga")
	                .param("description", "Yoga class")
	                .param("duration", "60"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("gym/createClass"))
	                .andExpect(MockMvcResultMatchers.model().attributeExists("successMessage"));
	    }
}
