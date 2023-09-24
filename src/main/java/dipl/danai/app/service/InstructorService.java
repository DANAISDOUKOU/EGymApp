package dipl.danai.app.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.repository.InstructorRepository;

@Service
public class InstructorService{

	Set<ClassOfSchedule> classes=new HashSet<>();

	@Autowired
	InstructorRepository instructorRepository;


	public void saveClass(ClassOfSchedule c) {
		classes.add(c);
	}

	public void saveInstructors(Instructor instructor) {
		instructor.setInstructorClass(classes);
		instructorRepository.save(instructor);
	}

	 public Instructor updateInstructorProfile(String email, String firstName, String lastName, String phoneNumber, String address,
				String city) {

		    Instructor instructor = instructorRepository.findByEmail(email);
		    if (instructor != null) {
		    	instructor.setInstructor_name(firstName);
		    	instructor.setInstructor_surname(lastName);
		    	instructor.setPhoneNumber(phoneNumber);
		    	instructor.setAddress(address);
		    	instructor.setCity(city);
		    	
		    	instructorRepository.save(instructor);

		        return instructor;
		    } else {
		        return null;
		    }
		}

	public Instructor getInstructor(String email) {
		Instructor instructor=instructorRepository.findByEmail(email);
		return instructor;
	}

	public List<Instructor> findAll(){
		List<Instructor> instructors=instructorRepository.findAll();
		return instructors;
	}

	public Instructor getById(Long id) {
		Instructor instructor=instructorRepository.findById(id).orElse(null);
		return instructor;
	}

	public Instructor getByEmail(String email) {
	    Instructor instructor = instructorRepository.findByEmail(email);
		return instructor;
	}

	public List<ClassOfSchedule> getClassesByGym(Long instructorId,Long gymId) {
	    List<ClassOfSchedule> classes = instructorRepository.findClassesByInstructorAndGym(instructorId, gymId);
	    return classes;
	}

	public Instructor getByName(String instructor) {
		return instructorRepository.findByName(instructor);
	}

	public void updateInstructorPicture(Instructor i,MultipartFile file) {
		 try {
			i.setPicture(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		 instructorRepository.save(i);
	}


}
