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
import org.springframework.web.bind.annotation.RequestParam;
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
    public String login(){
        return "auth/login";
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
	
	
	@GetMapping(value= {"/updateProfileName"})
	public String getProfileName(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user", user);
		return "updateProfileName";
	}
	
	@PostMapping(value= {"/updateProfileName"})
	public String setProfileName(Authentication authentication,Model model,@RequestParam(value="name") String name) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		userService.setValue(name, "firstName",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymService.getGymByEmail(user.getEmail());
			gymService.setValue(name,"gym_name",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteService.getAthlete(email);
			athleteService.setValue(name, "athlete_name", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorService.getInstructor(email);
			instructorService.setValue(name,"instructor_name",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	@GetMapping(value= {"/updateProfileSurname"})
	public String getProfileSurname(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user", user);
		return "updateProfileSurname";
	}
	
	@PostMapping(value= {"/updateProfileSurname"})
	public String setProfileSurname(Authentication authentication,Model model,@RequestParam(value="name") String name) {
		System.out.println("I am here");
		String email=authentication.getName();
		User user=userService.getUser(email);
		userService.setValue(name, "lastName",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymService.getGymByEmail(user.getEmail());
			gymService.setValue(name,"gym_surname",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteService.getAthlete(email);
			athleteService.setValue(name, "athlete_surname", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorService.getInstructor(email);
			instructorService.setValue(name,"instructor_surname",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	@GetMapping(value= {"/updateProfileCity"})
	public String getProfileCity(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user", user);
		return "updateProfileCity";
	}
	
	@PostMapping(value= {"/updateProfileCity"})
	public String setProfileCity(Authentication authentication,Model model,@RequestParam(value="City") String name) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		userService.setValue(name, "City",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymService.getGymByEmail(user.getEmail());
			gymService.setValue(name,"City",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteService.getAthlete(email);
			athleteService.setValue(name, "City", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorService.getInstructor(email);
			instructorService.setValue(name,"City",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	@GetMapping(value= {"/updateProfileAddress"})
	public String getProfileAddress(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user", user);
		return "updateProfileAddress";
	}
	
	@PostMapping(value= {"/updateProfileAddress"})
	public String setProfileAddress(Authentication authentication,Model model,@RequestParam(value="Address") String name) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		userService.setValue(name, "Address",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymService.getGymByEmail(user.getEmail());
			gymService.setValue(name,"Address",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteService.getAthlete(email);
			athleteService.setValue(name, "Address", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorService.getInstructor(email);
			instructorService.setValue(name,"Address",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	@GetMapping(value= {"/updateProfilePhone"})
	public String getProfilePhoneNumber(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		model.addAttribute("user", user);
		return "updateProfilePhone";
	}
	
	@PostMapping(value= {"/updateProfilePhone"})
	public String setProfilePhoneNumber(Authentication authentication,Model model,@RequestParam(value="phoneNumber") String name) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		userService.setValue(name, "PhoneNumber",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymService.getGymByEmail(user.getEmail());
			gymService.setValue(name,"PhoneNumber",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteService.getAthlete(email);
			athleteService.setValue(name, "PhoneNumber", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorService.getInstructor(email);
			instructorService.setValue(name,"PhoneNumber",instructor);
		}
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
}