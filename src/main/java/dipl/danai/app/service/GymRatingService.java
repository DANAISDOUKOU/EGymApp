package dipl.danai.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.GymRating;
import dipl.danai.app.repository.GymRatingRepository;

@Service
public class GymRatingService {
	 private final GymRatingRepository gymRatingRepository;

	    public GymRatingService(GymRatingRepository gymRatingRepository) {
	        this.gymRatingRepository = gymRatingRepository;
	    }

	    public void addRating(Gym gym, Athletes athlete, int rating) {
	        GymRating gymRating = new GymRating();
	        gymRating.setGym(gym);
	        gymRating.setAthlete(athlete);
	        gymRating.setRating(rating);
	        gymRatingRepository.save(gymRating);
	    }

	    public List<GymRating> getRatingsForGym(Gym gym) {
	        return gymRatingRepository.findByGym(gym);
	    }
	    
	    public double calculateAverageRating(Gym gym) {
	    	double currentTotalRatings = gymRatingRepository.calculateAverageRatingByGym(gym);
	    	return currentTotalRatings;
	    }
}
