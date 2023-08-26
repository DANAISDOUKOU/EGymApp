package dipl.danai.app.controller;

import java.sql.SQLException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Workout;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.WorkoutService;

@Controller
public class WorkoutController {
	
	@Autowired
	WorkoutService workoutService;
	@Autowired
	GymService gymService;
	
	 @GetMapping(value = {"/gym/createClass"})
	 public String createClass(Model model) {
		 model.addAttribute("workout",new Workout());
		 return "gym/createClass";
	 }
	 
	 @PostMapping(value = {"/gym/createClass"})
	 public String createClassWorkout(Authentication authentication,Model model, @Valid Workout workout, BindingResult bindingResult) throws SQLException{
	     Gym gym=gymService.getGymByEmail(authentication.getName()); 
		 workoutService.saveWorkout(workout);
	     gymService.addWorkout(workout);
	     gymService.saveGym(gym);
	     model.addAttribute("successMessage", "Class registered successfully!");
		return "gym/createClass";
	        
	 }
	 
	 
}
