package dipl.danai.controller;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.service.InstructorService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class InstructorControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorService instructorService;

    @Test
    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
    public void testInstructorPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/instructor/dashboard"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("instructor/dashboard"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "INSTRUCTOR")
    public void testGetGymsThatIWork() throws Exception {
    	Instructor mockInstructor = Mockito.mock(Instructor.class);
        Set<Gym> mockGyms = new HashSet<>();
        Gym gym1 = new Gym();
        gym1.setGym_id(1L);
        gym1.setGym_name("Gym 1");
        gym1.setAddress("Location 1");
        mockGyms.add(gym1);

        Gym gym2 = new Gym();
        gym2.setGym_id(2L);
        gym2.setGym_name("Gym 2");
        gym2.setAddress("Location 2");
        mockGyms.add(gym2);
        when(instructorService.getByEmail("testuser")).thenReturn(mockInstructor);
        when(mockInstructor.getGyms()).thenReturn(mockGyms);

        mockMvc.perform(MockMvcRequestBuilders.get("/instructor/gymsIWork"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("instructor/gymsIWork"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("gyms"));
    }
}