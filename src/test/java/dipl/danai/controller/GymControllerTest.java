package dipl.danai.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.GymService;

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
	
	
}
