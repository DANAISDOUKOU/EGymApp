package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long>{
	 //@Query("SELECT w FROM FitnessProgram f JOIN f.fitnessProgramWorkouts w JOIN f.fitnessProgramTimes t WHERE f.fitnessProgram_id = :fitnessProgram_id AND t.start = :startTime AND t.end = :endTime")
	 //List<Workout> findWorkoutByScheduleAndTime(Long fitnessProgram_id, Time startTime, Time endTime);
	//SELECT times.time, workouts.workout FROM times JOIN schedule_workouts ON times.time_id = schedule_workouts.time_id //JOIN workouts ON workouts.workout_id = schedule_workouts.workout_id WHERE schedule_workouts.schedule_id = 1
}
