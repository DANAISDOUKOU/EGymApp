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
		if(field.equals("instructor_name")) {
			instructor.setInstructor_name(value);
		}else if(field.equals("instructor_surname")) {
			instructor.setInstructor_surname(value);
		}else if(field.equals("PhoneNumber")) {
			instructor.setPhoneNumber(value);
		}else if(field.equals("Address")) {
			instructor.setAddress(value);
		}else if(field.equals("City")) {
			instructor.setCity(value);
		}
		instructorRepository.save(instructor);
	}
	
	
	 
}
