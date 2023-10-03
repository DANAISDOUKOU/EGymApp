package dipl.danai.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Workout;
import dipl.danai.app.model.WorkoutPopularity;
import dipl.danai.app.repository.WorkoutPopularityRepository;
import dipl.danai.app.service.PopularityCalculationService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ MockitoExtension.class}) 
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EGymApplication.class})
public class PopularityCalculationServiceTest {
	  @InjectMocks
	    private PopularityCalculationService popularityService;

	    @MockBean
	    private WorkoutPopularityRepository popularityRepository;
	    
	    @Test
	    public void testRecordUserInteraction_NewRecord() {
	        Gym gym = new Gym();
	        Workout workout = new Workout();
	        LocalDateTime now = LocalDateTime.now();

	        when(popularityRepository.findByGymAndWorkoutAndTimestampBetween(
	                any(Gym.class), any(Workout.class), any(LocalDateTime.class), any(LocalDateTime.class)))
	                .thenReturn(null);

	        popularityService.recordUserInteraction(gym, workout);

	        verify(popularityRepository, times(1)).save(any(WorkoutPopularity.class));
	    }
	    
	    
	    @Test
	    public void testRecordUserInteraction_ExistingRecord() {
	        Gym gym = new Gym();
	        Workout workout = new Workout();
	        LocalDateTime now = LocalDateTime.now();

	        // Mock behavior of findByGymAndWorkoutAndTimestampBetween to return an existing record
	        WorkoutPopularity existingRecord = new WorkoutPopularity();
	        existingRecord.setPopularityScore(3); // Existing popularity score
	        when(popularityRepository.findByGymAndWorkoutAndTimestampBetween(any(), any(), any(), any()))
	                .thenReturn(existingRecord);

	        int popularityScore = popularityService.recordUserInteraction(gym, workout);

	        // Verify that the existing record is updated with an increased popularity score
	        verify(popularityRepository).save(any());
	        assertEquals(4, popularityScore);
	    }

	    @Test
	    public void testGetTopPopularWorkoutsForGym() {
	        Gym gym = new Gym();
	        int numberOfTopWorkouts = 5;

	        // Mock behavior of findTopNByGymOrderByPopularityScoreDesc to return a list of popularity records
	        List<WorkoutPopularity> popularityRecords = new ArrayList<>();
	        for (int i = 0; i < numberOfTopWorkouts; i++) {
	            WorkoutPopularity popularityRecord = new WorkoutPopularity();
	            popularityRecord.setWorkout(new Workout());
	            popularityRecord.setPopularityScore(i);
	            popularityRecords.add(popularityRecord);
	        }
	        when(popularityRepository.findTopNByGymOrderByPopularityScoreDesc(any(), anyInt()))
	                .thenReturn(popularityRecords);

	        List<WorkoutPopularity> topPopularWorkouts = popularityService.getTopPopularWorkoutsForGym(gym, numberOfTopWorkouts);

	        // Verify that the expected number of top popular workouts is returned
	        assertEquals(numberOfTopWorkouts, topPopularWorkouts.size());
	    }
}
