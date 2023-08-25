package dipl.danai.app.model;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "gyms")
public class Gym {
	
	@Id
	@Column
	private Long gym_id;
	
	@Column 
	private String email;

	@Column
	private String gym_name;
	
	@Column
	private String gym_surname;	
	
	@Column
	private Double averageRating=0.0;
	
	@Column
	private Integer totalRatings;
	
	@ManyToMany
	@JoinTable(name="gym_workouuts",joinColumns=@JoinColumn(name="gym_id"),inverseJoinColumns=@JoinColumn(name="workout_id"))
	private List<Workout> gymWorkouts;
	
	@ManyToMany
	@JoinTable(name="gym_schedule", joinColumns=@JoinColumn(name="gym_id"),inverseJoinColumns=@JoinColumn(name="schedule_id"))
	private Collection<Schedule> gymSchedules;
	
	@ManyToMany
	@JoinTable(name="gym_members", joinColumns=@JoinColumn(name="gym_id"),inverseJoinColumns=@JoinColumn(name="athlete_id"))
	private Set<Athletes> gymMembers;
	
	@ManyToMany
	@JoinTable(name="gym_instructors", joinColumns=@JoinColumn(name="gym_id"),inverseJoinColumns=@JoinColumn(name="instructor_id"))
	private Set<Instructor> gymInstructors;
	
	@ManyToMany
	@JoinTable(name="gym_memberships", joinColumns=@JoinColumn(name="gym_id"),inverseJoinColumns=@JoinColumn(name="membership_type_id"))
	private List<MembershipType> gym_memberships;
	
	@OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<Room> rooms;
	
	@OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GymRating> ratingss;

	public Long getGym_id() {
		return gym_id;
	}

	public void setGym_id(Long gym_id) {
		this.gym_id = gym_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGym_name() {
		return gym_name;
	}

	public void setGym_name(String gym_name) {
		this.gym_name = gym_name;
	}

	public String getGym_surname() {
		return gym_surname;
	}

	public void setGym_surname(String gym_surname) {
		this.gym_surname = gym_surname;
	}

	public Collection<Schedule> getGymSchedules() {
		return gymSchedules;
	}

	public void setGymSchedules(Collection<Schedule> gymSchedules) {
		this.gymSchedules = gymSchedules;
	}

	public Set<Athletes> getGymMembers() {
		return gymMembers;
	}

	public void setGymMembers(Set<Athletes> gymMembers) {
		this.gymMembers = gymMembers;
	}

	public Set<Instructor> getGymInstructors() {
		return gymInstructors;
	}

	public void setGymInstructors(Set<Instructor> gymInstructors) {
		this.gymInstructors = gymInstructors;
	}

	public List<MembershipType> getGym_memberships() {
		return gym_memberships;
	}

	public void setGym_memberships(List<MembershipType> gym_memberships) {
		this.gym_memberships = gym_memberships;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public List<Workout> getGymWorkouts() {
		return gymWorkouts;
	}

	public void setGymWorkouts(List<Workout> gymWorkouts) {
		this.gymWorkouts = gymWorkouts;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public int getTotalRatings() {
		return totalRatings;
	}

	public void setTotalRatings(int totalRatings) {
		this.totalRatings = totalRatings;
	}

	public List<GymRating> getRatingss() {
		return ratingss;
	}

	public void setRatingss(List<GymRating> ratingss) {
		this.ratingss = ratingss;
	}

	

	
	
}
