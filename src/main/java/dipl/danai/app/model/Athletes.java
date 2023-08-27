package dipl.danai.app.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="athletes")
public class Athletes {
	
	@Id
	@Column
	public Long athlete_id;
		
	@Column
	private String athlete_name;
	
	@Column
	private String athlete_surname;
	
	
	@ManyToMany(mappedBy="participants", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<ClassOfSchedule> classes;
	
	@ManyToMany(mappedBy="gymMembers")
	private Set<Gym> gyms;
	
	@Column
	private String contact;
	
	@Column 
	private String email;
	
	 @Column
	 private String Address;
	    
	 @Column
	 private String City;
	    
	 @Column(name = "phone_number")
	 @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
	 @Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits")
	 private String phoneNumber;
	
	 public Long getAthlete_id() {
		return athlete_id;
	 }

	 public void setAthlete_id(Long athlete_id) {
		 this.athlete_id = athlete_id;
	 }

	 public String getAthlete_name() {
		 return athlete_name;
	 }

	 public void setAthlete_name(String athlete_name) {
		 this.athlete_name = athlete_name;
	 }

	 public String getAthlete_surname() {
		 return athlete_surname;
	 }

	 public void setAthlete_surname(String athlete_surname) {
		 this.athlete_surname = athlete_surname;
	 }

	 public List<ClassOfSchedule> getClasses() {
		 return classes;
	 }

	 public void setClasses(List<ClassOfSchedule> classes) {
		 this.classes = classes;
	 }

	 public Set<Gym> getGyms() {
		 return gyms;
	 }

	 public void setGyms(Set<Gym> gyms) {
		 this.gyms = gyms;
	 }

	 public String getContact() {
		 return contact;
	 }

	 public void setContact(String contact) {
		 this.contact = contact;
	 }

	 public String getEmail() {
		 return email;
	 }

	 public void setEmail(String email) {
		 this.email = email;
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
	 
}