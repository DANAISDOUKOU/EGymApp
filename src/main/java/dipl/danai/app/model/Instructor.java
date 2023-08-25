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

	@ManyToMany(mappedBy="gymInstructors")
	private Set<Gym> gyms;
	
	@OneToMany
	@JoinTable(name="instructor_classes", joinColumns=@JoinColumn(name="instructor_id"),inverseJoinColumns=@JoinColumn(name="class_id"))
	private Set<ClassOfSchedule> instructorClass;
	
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

	
	
}
