package dipl.danai.controller;

import static org.mockito.Mockito.when;

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
import dipl.danai.app.model.Gym;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.MembershipService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class MembershipControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembershipService membershipService;

    @MockBean
    private GymService gymService;

    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testCreateMembershipType() throws Exception {
        Gym gym = new Gym();
        gym.setGym_id(1L);
        gym.setEmail("testuser@example.com");
        when(gymService.getGymByEmail("testuser")).thenReturn(gym);
        mockMvc.perform(MockMvcRequestBuilders.get("/gym/createMembershipType"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/createMembershipType"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("gymId"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("membership"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "GYM")
    public void testCreateMembershipTypePost() throws Exception {
        Gym gym = new Gym();
        gym.setGym_id(1L);

        when(gymService.getGymById(1L)).thenReturn(gym);

        mockMvc.perform(MockMvcRequestBuilders.post("/gym/createMembershipType")
                .param("gymId", "1")
                .param("name", "Basic")
                .param("description", "Basic Membership")
                .param("price", "50.0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("gym/createMembershipType"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("successMessage"));
    }
}
