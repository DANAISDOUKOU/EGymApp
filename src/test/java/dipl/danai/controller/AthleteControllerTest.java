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
import static org.mockito.Mockito.when;
import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.service.AthleteService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class AthleteControllerTest {
	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private AthleteService athleteService;

    @Test
    @WithMockUser(username="example@gmail.com",authorities = "ATHLETE")
    public void testAthletePage() throws Exception {
    	 Athletes mockAthlete = new Athletes(356L,"kati","kati","example@gmail.com","kapou","Ioannina","0123456789");

         when(athleteService.getAthlete("example@gmail.com")).thenReturn(mockAthlete);

         mockMvc.perform(MockMvcRequestBuilders.get("/athlete/dashboard"))
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.view().name("athlete/dashboard"));
     
    }

    @Test
    @WithMockUser(authorities = "ATHLETE")
    public void testSeeAllGyms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/athlete/seeAllGyms"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("athlete/seeAllGyms"));
    }

    @Test
    @WithMockUser(username="example@gmail.com",authorities = "ATHLETE")
    public void testSeeGymsThatIamMember() throws Exception {
    	 Athletes mockAthlete = new Athletes(356L,"kati","kati","example@gmail.com","kapou","Ioannina","0123456789");

         when(athleteService.getAthlete("example@gmail.com")).thenReturn(mockAthlete);
        mockMvc.perform(MockMvcRequestBuilders.get("/athlete/GymsThatIAmMember"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("athlete/gymsThatIAmMember"));
    }

    @Test
    @WithMockUser(authorities = "ATHLETE")
    public void testViewAttendanceHistory() throws Exception {
    	
        mockMvc.perform(MockMvcRequestBuilders.get("/athlete/history")
                .param("gymId", "322")) 
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("athlete/history"));
    }

    @Test
    @WithMockUser(authorities = "ATHLETE")
    public void testRateClass() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rate-class")
                .param("classOfScheduleId", "383") 
                .param("gymId", "322")
                .param("instructorRating", "5.0")
                .param("accuracyRating", "4.0")
                .param("crowdingRating", "3.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/athlete/history"));
    }
}
