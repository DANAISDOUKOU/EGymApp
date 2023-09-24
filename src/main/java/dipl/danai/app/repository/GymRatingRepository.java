package dipl.danai.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Gym;
import dipl.danai.app.model.GymRating;

@Repository
public interface GymRatingRepository extends JpaRepository<GymRating, Long> {
	 List<GymRating> findByGym(Gym gym);

	 @Query("SELECT AVG(gr.rating) FROM GymRating gr WHERE gr.gym = ?1")
	 Double calculateAverageRatingByGym(Gym gym);

	 @Query("SELECT COUNT(gr) FROM GymRating gr WHERE gr.gym = ?1")
	 Integer calculateTotalRatingsByGym(Gym gym);
}
