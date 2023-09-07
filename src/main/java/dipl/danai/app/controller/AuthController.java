package dipl.danai.app.controller;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Role;
import dipl.danai.app.model.User;
import dipl.danai.app.service.AthleteService;
import dipl.danai.app.service.EmailService;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.InstructorService;
import dipl.danai.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;
     
    @Autowired
    private GymService gymService;
    
    @Autowired 
    private AthleteService athleteService;
    
    @Autowired 
    private InstructorService instructorService;
    
    @Autowired 
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
  
  
    @GetMapping("/login")
    public String login(Model model,HttpServletRequest request){
    	 request.getSession().setAttribute("justLoggedIn", true);
        return "auth/login";
    }
    @RequestMapping("/clearLoggedInSessionAttribute")
    public String clearLoggedInSessionAttribute(HttpServletRequest request) {
        request.getSession().removeAttribute("justLoggedIn");
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @Valid User user, BindingResult bindingResult) throws SQLException{
        if(bindingResult.hasErrors()){
            model.addAttribute("successMessage", "User registered successfully!");
            model.addAttribute("bindingResult", bindingResult);
            return "auth/register";
        }
        List<Object> userPresentObj = userService.isUserPresent(user);
        if((Boolean) userPresentObj.get(0)){
            model.addAttribute("successMessage", userPresentObj.get(1));
            return "auth/register";
        }

        userService.saveUser(user);
        model.addAttribute("successMessage", "User registered successfully!");
        
        Long id = user.getId();
        String name=user.getFirstName();
        String surname=user.getLastName();
        String email=user.getEmail();
        String city=user.getCity();
        String address=user.getAddress();
        String phoneNumber=user.getPhoneNumber();
        if (user.getRole() == Role.ATHLETE) {
            userService.insertAthlete(id, name, surname, email, address, city, phoneNumber);
        } else if (user.getRole() == Role.GYM) {
        	userService.insertGym(id, name, surname, email, address, city, phoneNumber);
        } else if (user.getRole() == Role.INSTRUCTOR) {
        	userService.insertInstructor(id, name, surname, email, address, city, phoneNumber);
        }
        return "auth/login";
    }
    
	@GetMapping(value= {"/profile"})
	public String showProfile(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user",user);
		return "profile";
	}

	@GetMapping("/forgotPassword")
	public String showForgotPasswordPage(Model model) {
		return "forgotPassword";
	}
	  
	@PostMapping("/forgotPassword")
	public String forgotPassword(@RequestParam("email") String email, Model model) {
		String resetToken =userService.generateResetToken();
	    User user = userService.getUser(email);
	    if (user != null) {
	    	Date expiryDate = userService.calculateExpiryDate();
	    	user.setResetToken(resetToken);
	    	user.setResetTokenExpiryDate(expiryDate);
	    	userService.updateSaveUser(user);
	        String resetLink = "http://localhost:8080/resetPassword?token=" + resetToken;
	        String emailContent = "Click the following link to reset your password: " + resetLink;
	        emailService.sendEmail(email, "Password Reset", emailContent);
	    }
	   return "forgot_password_success_page";
	}
	  
	@GetMapping("/resetPassword")
	public String showResetPasswordPage(@RequestParam("token") String resetToken, Model model) {
		User user =userService.validateResetToken(resetToken); 
	    if (user != null) {
	    	model.addAttribute("resetToken", resetToken);
	        return "resetPassword";
	    } else {
	        return "invalidResetToken";
	    }
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam("token") String resetToken,@RequestParam("password") String newPassword,Model model) {
	    User user = userService.validateResetToken(resetToken); 
	    if (user != null) {
	        user.setPassword(passwordEncoder.encode(newPassword));
	    	userService.updateSaveUser(user);
	        return "passwordResetSuccess";
	    } else {
	        return "invalidResetToken";
	    }
	}
	
	 @PostMapping("/updateProfile")
	    public String updateProfile(@RequestParam("field") String field,
	                                @RequestParam("value") String value,
	                                Authentication authentication) {

		 	String email=authentication.getName();
			User user=userService.getUser(email);
			userService.setValue(value, field, user);
			if(user.getRole()==Role.GYM) {
				Gym gym=gymService.getGymByEmail(user.getEmail());
				gymService.setValue(value,field,gym);
			}else if(user.getRole()==Role.ATHLETE) {
				Athletes athlete=athleteService.getAthlete(email);
				athleteService.setValue(value, field, athlete);
			}else if(user.getRole()==Role.INSTRUCTOR) {
				Instructor instructor=instructorService.getInstructor(email);
				instructorService.setValue(value,field,instructor);
			}

	        return "redirect:/profile"; 
	    }
	 
	 @GetMapping("/visitor")
	 public String visitor(Model model) {
		 model.addAttribute("gyms",gymService.getGyms());
		 return "visitor/visitor-gyms";
	 }
}