package dipl.danai.app.model;


import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="instructors")
public class Instructor {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long instructor_id;
	
	@Column
	private String instructor_name;
	
	@Column
	private String instructor_surname;
	
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

	@ManyToMany(mappedBy="gymInstructors")
	private Set<Gym> gyms;
	
	@OneToMany
	@JoinTable(name="instructor_classes", joinColumns=@JoinColumn(name="instructor_id"),inverseJoinColumns=@JoinColumn(name="class_id"))
	private Set<ClassOfSchedule> instructorClass;
	
	public Instructor(Long id, String name, String surname, String email2, String address2, String city2,
			String phoneNumber2) {
		this.instructor_id=id;
		this.instructor_name=name;
		this.instructor_surname=surname;
		this.email=email2;
		this.Address=address2;
		this.City=city2;
		this.phoneNumber=phoneNumber2;
	}

	public Long getInstructor_id() {
		return instructor_id;
	}

	public void setInstructor_id(Long instructor_id) {
		this.instructor_id = instructor_id;
	}

	public String getInstructor_name() {
		return instructor_name;
	}

	public void setInstructor_name(String instructor_name) {
		this.instructor_name = instructor_name;
	}

	public String getInstructor_surname() {
		return instructor_surname;
	}

	public void setInstructor_surname(String instructor_surname) {
		this.instructor_surname = instructor_surname;
	}

	public Set<Gym> getGyms() {
		return gyms;
	}

	public void setGyms(Set<Gym> gyms) {
		this.gyms = gyms;
	}

	public Set<ClassOfSchedule> getInstructorClass() {
		return instructorClass;
	}

	public void setInstructorClass(Set<ClassOfSchedule> instructorClass) {
		this.instructorClass = instructorClass;
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
