package dipl.danai.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.model.AthleteClassScheduleReservation;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.WeeksReservedRepository;

@Service
public class AthleteService {

	@Autowired
	private AthleteRepository athleteRepository;
	
	@Autowired
	private WeeksReservedRepository weekReservedRepo;

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
		System.out.println("aaaaaaaaaa "+email);
		if(athlete!=null) {
			System.out.println("hereee");
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

	public Integer findWeeks(Athletes athlete, ClassOfSchedule classOfSchedule) {
		return weekReservedRepo.findReservedWeekByClassOfScheduleAndAthlete(classOfSchedule, athlete);
	}

	public void saveReservetions(AthleteClassScheduleReservation reserve) {
		weekReservedRepo.save(reserve);
		
	}
}
