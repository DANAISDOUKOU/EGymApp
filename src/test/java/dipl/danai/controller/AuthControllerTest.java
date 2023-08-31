package dipl.danai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Role;
import dipl.danai.app.model.User;
import dipl.danai.app.service.EmailService;
import dipl.danai.app.service.UserService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
	
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @MockBean
	 private UserService userService;
	 @MockBean
	 private EmailService emailService;
	 
	 
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
	 
	 @Test
	 public void testLogin() throws Exception {
	     mockMvc.perform(MockMvcRequestBuilders.post("/login")
	             .param("email", "danai-sdk@hotmail.com")
	             .param("password", "danai1998"))
	     .andExpect(MockMvcResultMatchers.status().isFound()) 
         .andExpect(MockMvcResultMatchers.redirectedUrl("athlete/dashboard")); 
	 }
	 

	 @Test
	 public void testLogout() throws Exception {
		 mockMvc.perform(MockMvcRequestBuilders.post("/logout")
            .with(SecurityMockMvcRequestPostProcessors.user("testuser").roles("ATHLETE")))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) 
            .andExpect(MockMvcResultMatchers.redirectedUrl("/auth/login")); 
	 }
	 
	  @Test
	    public void testForgotPassword() throws Exception {
	        mockMvc.perform(MockMvcRequestBuilders.post("/forgotPassword")
	                .param("email", "user@example.com"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.view().name("forgot_password_success_page"));  
	    }

}
