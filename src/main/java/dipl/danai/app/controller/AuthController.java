package dipl.danai.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
    
    @PostMapping("/uploadProfilePicture")
	@Transactional
	public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,Authentication authentication) {
		 if (!file.isEmpty()) {
		 try {
	               userService.setTemporaryProfilePictureBytes(file.getBytes());
	            } catch (IOException e) {
	                return "error-page"; 
	            }
	        } else {
	            return "redirect:/error-page"; 
	        }

	        return "redirect:/register"; 
	    }

    @PostMapping("/register")
    public String registerUser(Model model, @Valid User user, BindingResult bindingResult) throws SQLException, IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("successMessage", "User not registered!");
            model.addAttribute("bindingResult", bindingResult);
            return "auth/register";
        }
        userService.saveUser(user);
        byte[] profilePictureBytes = userService.getTemporaryProfilePictureBytes();
        if (profilePictureBytes != null) {
            user.setProfilePicture(profilePictureBytes);
            userService.updateSaveUser(user);
            userService.clearTemporaryProfilePictureBytes();
        }
        model.addAttribute("successMessage", "User registered successfully!");
        Long id = user.getId();
        String name = user.getFirstName();
        String surname = user.getLastName();
        String email = user.getEmail();
        String city = user.getCity();
        String address = user.getAddress();
        String phoneNumber = user.getPhoneNumber();
        if (user.getRole() == Role.ATHLETE) {
            userService.insertAthlete(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
        } else if (user.getRole() == Role.GYM) {
            userService.insertGym(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
        } else if (user.getRole() == Role.INSTRUCTOR) {
            userService.insertInstructor(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
        }
        return "auth/login";
    }

	@GetMapping(value= {"/profile"})
	public String showProfile(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userService.getUser(email);
		byte[] profilePicture=user.getProfilePicture();
		String base64ProfilePicture = Base64.getEncoder().encodeToString(profilePicture);
        model.addAttribute("base64ProfilePicture", base64ProfilePicture);
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
	public String updateProfile(@RequestParam("email") String email,
	                            @RequestParam("firstName") String firstName,
	                            @RequestParam("lastName") String lastName,
	                            @RequestParam("phoneNumber") String phoneNumber,
	                            @RequestParam("Address") String address,
	                            @RequestParam("City") String city,
	                            Model model) {
	    User user=userService.getUser(email);
		User updatedUser = userService.updateUserProfile(email, firstName, lastName, phoneNumber, address, city);
	    model.addAttribute("user", updatedUser);
	    if(user.getRole()==Role.GYM) {
	    	gymService.updateGymProfile(email, firstName, lastName, phoneNumber, address, city);
	    }
	    else if(user.getRole()==Role.ATHLETE) {
			athleteService.updateAthleteProfile(email, firstName, lastName, phoneNumber, address, city);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			instructorService.updateInstructorProfile(email, firstName, lastName, phoneNumber, address, city);
		}
	    return "redirect:/profile";
	}
	
	 @GetMapping("/visitor")
	 public String visitor(Model model) {
		 List<Gym> gyms=gymService.getGyms();
		 for (Gym gym : gyms) {
	 			byte[] pictureBytes = gym.getPicture(); 
	 			String pictureBase64 = Base64.getEncoder().encodeToString(pictureBytes);
	 			gym.setProfilePictureBase64(pictureBase64); 
	 		}
		 model.addAttribute("gyms",gyms);
		 return "visitor/visitor-gyms";
	 }

	 @PostMapping("/updateProfilePicture")
	 @Transactional
	    public String updateProfilePicture(@RequestParam("profilePicture") MultipartFile file,Authentication authentication) {
		 User user=userService.getUser(authentication.getName());
		 if (!file.isEmpty()) {
			 try {
	                byte[] profilePictureBytes = file.getBytes();
	                user.setProfilePicture(profilePictureBytes);
	                userService.updateSaveUser(user);
	                if(user.getRole()==Role.GYM) {
	        	    	Gym gym=gymService.getGymByEmail(user.getEmail());
	        	    	gymService.updateGymPicture(gym,file);
	        	    }
	        	    else if(user.getRole()==Role.ATHLETE) {
	        			Athletes athlete=athleteService.getAthlete(user.getEmail());
	        			athleteService.updateAthletePicture(athlete,file);
	        		}else if(user.getRole()==Role.INSTRUCTOR) {
	        			Instructor instructor=instructorService.getInstructor(user.getEmail());
	        			instructorService.updateInstructorPicture(instructor,file);
	        		}
	            } catch (IOException e) {
	                return "error-page"; 
	            }
	        } else {
	            return "redirect:/error-page"; 
	        }

	        return "redirect:/profile"; 
	    }
}