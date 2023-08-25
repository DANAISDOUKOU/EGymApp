package dipl.danai.app.controller;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Role;
import dipl.danai.app.model.User;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.UserRepository;
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
import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;
   
    @Autowired
    private DataSource dataSource;
    
    @Autowired 
    private UserRepository userRepo;
    
    @Autowired 
    private GymRepository gymRepo;
   
    @Autowired
    private GymService gymService;
    
    @Autowired 
    private AthleteService athleteService;
    
    @Autowired
    private AthleteRepository athleteRepo;
    
    @Autowired 
    private InstructorService instructorService;
    
    @Autowired
    private InstructorRepository instructorRepo;
    
    @Autowired 
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    String query=("INSERT INTO athletes(athlete_id,athlete_name,athlete_surname,email)"+"VALUES(?,?,?,?)");
    String query2=("INSERT INTO gyms(gym_id,gym_name,gym_surname,email)"+"VALUES(?,?,?,?)");
    String query3=("INSERT INTO instructors(instructor_id,instructor_name,instructor_surname,email)"+"VALUES(?,?,?,?)");
  
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
        
        if(user.getRole()==Role.ATHLETE){
        	PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                pstmt.setLong(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3,surname);
                pstmt.setString(4,email);
                pstmt.executeUpdate();
        	
        	}
        else if(user.getRole()==Role.GYM) {
        	PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query2,Statement.RETURN_GENERATED_KEYS);
                pstmt.setLong(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3,surname);
                pstmt.setString(4,email);
                pstmt.executeUpdate();	
        	}
        		
        else if(user.getRole()==Role.INSTRUCTOR){
        		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query3,Statement.RETURN_GENERATED_KEYS); 
                pstmt.setLong(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3,surname);
                pstmt.setString(4,email);

                pstmt.executeUpdate();  	
        }		
        return "auth/login";
    }
    
	@GetMapping(value= {"/profile"})
	public String showProfile(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userRepo.findByEmail(email);
		model.addAttribute("user",user);
		return "profile";
	}
	
	
	@GetMapping(value= {"/updateProfileName"})
	public String getProfileName(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userRepo.findByEmail(email);
		model.addAttribute("user", user);
		return "updateProfileName";
	}
	
	@PostMapping(value= {"/updateProfileName"})
	public String setProfileName(Authentication authentication,Model model,@RequestParam(value="name") String name) {
		String email=authentication.getName();
		User user=userRepo.findByEmail(email);
		userService.setValue(name, "firstName",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymRepo.findByEmail(user.getEmail());
			gymService.setValue(name,"gym_name",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteRepo.findByEmail(email);
			athleteService.setValue(name, "athlete_name", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorRepo.findByEmail(email);
			instructorService.setValue(name,"instructor_name",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	@GetMapping(value= {"/updateProfileSurname"})
	public String getProfileSurname(Authentication authentication,Model model) {
		String email=authentication.getName();
		User user=userRepo.findByEmail(email);
		model.addAttribute("user", user);
		return "updateProfileSurname";
	}
	
	@PostMapping(value= {"/updateProfileSurname"})
	public String setProfileSurname(Authentication authentication,Model model,@RequestParam(value="name") String name) {
		System.out.println("I am here");
		String email=authentication.getName();
		User user=userRepo.findByEmail(email);
		userService.setValue(name, "lastName",user);
		if(user.getRole()==Role.GYM) {
			Gym gym=gymRepo.findByEmail(user.getEmail());
			gymService.setValue(name,"gym_surname",gym);
		}else if(user.getRole()==Role.ATHLETE) {
			Athletes athlete=athleteRepo.findByEmail(email);
			athleteService.setValue(name, "athlete_surname", athlete);
		}else if(user.getRole()==Role.INSTRUCTOR) {
			Instructor instructor=instructorRepo.findByEmail(email);
			instructorService.setValue(name,"instructor_surname",instructor);
		}
		model.addAttribute("user",user);
		return "profile";
	}
	
	//Add for address and for phone
	  @GetMapping("/forgotPassword")
	    public String showForgotPasswordPage(Model model) {
	        return "forgotPassword";
	    }
	  
	  @PostMapping("/forgotPassword")
	  public String forgotPassword(@RequestParam("email") String email, Model model) {

		  String resetToken =userService.generateResetToken();
	      User user = userRepo.findByEmail(email);
	      if (user != null) {
	    	  Date expiryDate = userService.calculateExpiryDate();
	    	  user.setResetToken(resetToken);
	    	  user.setResetTokenExpiryDate(expiryDate);
	          userRepo.save(user);
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
	  public String resetPassword(
	          @RequestParam("token") String resetToken,
	          @RequestParam("password") String newPassword,
	          Model model) {
	      User user = userService.validateResetToken(resetToken); 
	      if (user != null) {
	          user.setPassword(passwordEncoder.encode(newPassword));
	          userRepo.save(user);
	          return "passwordResetSuccess";
	      } else {
	          return "invalidResetToken";
	      }
	  }
	  
	

}