package dipl.danai.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="gym_ratings")
public class GymRating {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gym_rating_id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne
    @JoinColumn(name = "athlete_id")
    private Athletes athlete;

    @Column
    private int rating;

	public Long getGymRatingId() {
		return gym_rating_id;
	}

	public void setGymRatingId(Long id) {
		this.gym_rating_id = id;
	}

	public Gym getGym() {
		return gym;
	}

	public void setGym(Gym gym) {
		this.gym = gym;
	}

	public Athletes getAthlete() {
		return athlete;
	}

	public void setAthlete(Athletes athlete) {
		this.athlete = athlete;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
    
    
}
