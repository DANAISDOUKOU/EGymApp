package dipl.danai.app.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.WorkoutRepository;

@Service
public class WorkoutService{
	@Autowired
	WorkoutRepository workoutRepository;
	
	public void saveWorkout(Workout workout) {
		workoutRepository.save(workout);
	}

	public List<Workout> findAll() {
		var workouts=(List<Workout>) workoutRepository.findAll();
		return workouts;
	}
}