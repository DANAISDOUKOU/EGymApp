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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

	@Column
	private String Address;

	@Column
    private String City;

	@Column
	private Double latitude;

	@Column
	private Double longitude;

	@Column(name = "phone_number")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
	@Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits")
    private String phoneNumber;

	@Lob
	@Column(name = "picture")
	private byte[] picture;

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

	 @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
	 private List<MembershipType> gymMemberships ;

	@OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<Room> rooms;

	@OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GymRating> ratingss;
	
	@Column(name = "use_membership_types_as_offers")
	private boolean useMembershipTypesAsOffers=false;
	
	private String profilePictureBase64;

	public Gym(Long id, String name, String surname, String email2, String address2, String city2,
			String phoneNumber2,byte[] profilePictureBytes) {
		this.gym_id=id;
		this.gym_name=name;
		this.gym_surname=surname;
		this.email=email2;
		this.Address=address2;
		this.City=city2;
		this.phoneNumber=phoneNumber2;
		this.picture=profilePictureBytes;
	}

	public Gym() {}

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
		return gymMemberships;
	}

	public void setGym_memberships(List<MembershipType> gym_memberships) {
		this.gymMemberships = gym_memberships;
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

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public void setTotalRatings(Integer totalRatings) {
		this.totalRatings = totalRatings;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<MembershipType> getGymMemberships() {
		return gymMemberships;
	}

	public void setGymMemberships(List<MembershipType> gymMemberships) {
		this.gymMemberships = gymMemberships;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] profilePicture) {
		this.picture = profilePicture;
	}
	public String getProfilePictureBase64() {
	    return profilePictureBase64;
	}

	public void setProfilePictureBase64(String profilePictureBase64) {
	    this.profilePictureBase64 = profilePictureBase64;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public boolean isUseMembershipTypesAsOffers() {
		return useMembershipTypesAsOffers;
	}

	public void setUseMembershipTypesAsOffers(boolean useMembershipTypesAsOffers) {
		this.useMembershipTypesAsOffers = useMembershipTypesAsOffers;
	}

	
}
