package dipl.danai.app.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Times;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.TimesRepository;
import dipl.danai.app.repository.WorkoutRepository;

@Service
public class TimeService{

	@Autowired
	TimesRepository timeRepository;
	
	@Autowired
	WorkoutRepository workoutRepository;
	
	Set<Workout> workouts=new HashSet<>();
	
	public void saveWorkout(Workout workout) {
		workouts.add(workout);	
	}

	
	public Times saveTime(Times time) {
		
		Times checkTime=timeRepository.findByStartTime(time.getTime_start());
		if(checkTime==null) {
			return timeRepository.save(time);
		}else {
			return checkTime;
		}
		
		
	}
	
	
}
