package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Workout;
import dipl.danai.app.model.WorkoutPopularity;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutPopularityRepository extends JpaRepository<WorkoutPopularity, Long> {
    
    @Query("SELECT wp FROM WorkoutPopularity wp " +
           "WHERE wp.gym = :gym " +
           "AND wp.workout = :workout " +
           "AND wp.timestamp BETWEEN :startOfMonth AND :now")
    WorkoutPopularity findByGymAndWorkoutAndTimestampBetween(
        Gym gym, Workout workout, LocalDateTime startOfMonth, LocalDateTime now
    );
    @Query("SELECT wp FROM WorkoutPopularity wp WHERE wp.gym = ?1 ORDER BY wp.popularityScore DESC")
    List<WorkoutPopularity> findTopNByGymOrderByPopularityScoreDesc(Gym gym, int i);
	
    
    List<WorkoutPopularity> findByGymAndTimestampBetween(Gym gym, LocalDateTime startOfMonth, LocalDateTime now);

}

