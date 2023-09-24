package dipl.danai.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="class_ratings")
public class ClassRating {
	 @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long class_rating_id;

	 @OneToOne
	 @JoinColumn(name = "gym_id")
	 private Gym gym;

	 @ManyToOne
	 @JoinColumn(name = "athlete_id")
	 private Athletes athlete;

	 @ManyToOne
	 @JoinColumn(name = "classOfScheduleId")
	 private ClassOfSchedule classOfSchedule;

	 @ManyToOne
	 @JoinColumn(name = "instructor_id")
	 private Instructor instructor;

	 @Column
	 private double classRating;

	public Long getClass_rating_id() {
		return class_rating_id;
	}

	public void setClass_rating_id(Long class_rating_id) {
		this.class_rating_id = class_rating_id;
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

	public ClassOfSchedule getClassOfSchedule() {
		return classOfSchedule;
	}

	public void setClassOfSchedule(ClassOfSchedule classOfSchedule) {
		this.classOfSchedule = classOfSchedule;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}


	public double getClassRating() {
		return classRating;
	}

	public void setClassRating(double classRating) {
		this.classRating = classRating;
	}



}
