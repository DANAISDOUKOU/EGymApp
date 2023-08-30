package dipl.danai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Role;
import dipl.danai.app.model.User;
import dipl.danai.app.service.UserService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
	
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @MockBean
	 private UserService userService;
	 
	 @Test
	 public void testLoginPage() throws Exception {
	       mockMvc.perform(MockMvcRequestBuilders.get("/login"))
	           .andExpect(MockMvcResultMatchers.status().isOk())
	           .andExpect(MockMvcResultMatchers.view().name("auth/login"));
	 }
	 
	 @Test
	 public void testRegisterUserSuccess() throws Exception {
	      User user = new User();
	      user.setRole(Role.ATHLETE);
	      user.setEmail("abcdf@gmail.com");
	      user.setPassword("kati1234");
	      user.setFirstName("kati");
	      user.setLastName("oupsss");
	      when(userService.isUserPresent(any(User.class))).thenReturn(Arrays.asList(false, null));
	      mockMvc.perform(MockMvcRequestBuilders.post("/register")
	          .flashAttr("user", user))
	          .andExpect(MockMvcResultMatchers.status().isOk())
	          .andExpect(MockMvcResultMatchers.view().name("auth/login"));
	      verify(userService).saveUser(any(User.class));
	 }
	 
}
