package dipl.danai.app.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	
	public void setValue(String value,String field, Instructor instructor) {
		if(field.equals("name")) {
			instructor.setInstructor_name(value);
		}else if(field.equals("surname")) {
			instructor.setInstructor_surname(value);
		}else if(field.equals("number")) {
			instructor.setPhoneNumber(value);
		}else if(field.equals("address")) {
			instructor.setAddress(value);
		}else if(field.equals("city")) {
			instructor.setCity(value);
		}
		instructorRepository.save(instructor);
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
	
	 
}
