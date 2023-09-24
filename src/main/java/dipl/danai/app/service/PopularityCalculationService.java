package dipl.danai.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Workout;
import dipl.danai.app.model.WorkoutPopularity;
import dipl.danai.app.repository.WorkoutPopularityRepository;

@Service
public class PopularityCalculationService {

    @Autowired
    private WorkoutPopularityRepository popularityRepository;
    
    private Gym gym;
    
    public int recordUserInteraction(Gym gym, Workout workout) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        WorkoutPopularity popularityRecord = popularityRepository.findByGymAndWorkoutAndTimestampBetween(
                gym, workout, startOfMonth, now);

        if (popularityRecord == null) {
            popularityRecord = new WorkoutPopularity();
            popularityRecord.setGym(gym);
            popularityRecord.setWorkout(workout);
            popularityRecord.setPopularityScore(1);
            popularityRecord.setTimestamp(now);
        } else {
            popularityRecord.setPopularityScore(popularityRecord.getPopularityScore() + 1);
            popularityRecord.setTimestamp(now);
        }
        popularityRepository.save(popularityRecord);
		return popularityRecord.getPopularityScore();
    }

	public List<WorkoutPopularity> getTopPopularWorkoutsForGym(Gym gym, int i) {
		 return popularityRepository.findTopNByGymOrderByPopularityScoreDesc(gym, i);
	}
	
	public void setScheduledGym(Gym gym) {
        this.gym = gym;
    }
	  @Scheduled(cron = "0 0 0 1 * ?")
	public void updatePopularityScoresForGym() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<WorkoutPopularity> popularityRecords = popularityRepository.findByGymAndTimestampBetween(
                gym, startOfMonth, now);

        for (WorkoutPopularity popularityRecord : popularityRecords) {
    
            int updatedScore = recordUserInteraction(gym, popularityRecord.getWorkout());
            popularityRecord.setPopularityScore(updatedScore);
        }

        popularityRepository.saveAll(popularityRecords);
    }

}
