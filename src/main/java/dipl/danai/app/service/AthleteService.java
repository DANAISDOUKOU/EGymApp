package dipl.danai.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.repository.AthleteRepository;

@Service
public class AthleteService {

	@Autowired
	private AthleteRepository athleteRepository;

	public Athletes getAthlete(String Email) {
		 Athletes athlete = athleteRepository.findByEmail(Email);
		 return athlete;
	}

	public List<ClassOfSchedule> findClasses(Athletes athlete){
		List<ClassOfSchedule> attendedClasses = athleteRepository.findAttendedClassesByParticipantsContaining(athlete);
		return attendedClasses;
	}

	public Athletes updateAthleteProfile(String email, String firstName, String lastName, String phoneNumber, String address,
			String city) {
		Athletes athlete=athleteRepository.findByEmail(email);
		if(athlete!=null) {
			athlete.setAthlete_name(firstName);
			athlete.setAthlete_surname(lastName);
			athlete.setPhoneNumber(phoneNumber);
			athlete.setAddress(address);
			athlete.setCity(city);
			 
			athleteRepository.save(athlete);
			return athlete;
		}
		else {
			return null;
		}
	}

	public void save(Athletes athlete) {
		athleteRepository.save(athlete);
	}

	public Athletes getById(Long id) {
		return athleteRepository.findById(id).orElse(null);
	}

	public void updateAthletePicture(Athletes a,MultipartFile file) {
		try {
			a.setPicture(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		athleteRepository.save(a);
	}
}
