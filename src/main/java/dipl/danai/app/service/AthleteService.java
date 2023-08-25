package dipl.danai.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.repository.AthleteRepository;

@Service
public class AthleteService {
	
	@Autowired 
	private AthleteRepository athleteRepository;
	
	public void setValue(String value,String field, Athletes athlete) {
		if(field.equals("athlete_name")) {
			athlete.setAthlete_name(value);
		}else if(field.equals("athlete_surname")) {
			athlete.setAthlete_surname(value);
		}else if(field.equals("PhoneNumber")) {
			athlete.setPhoneNumber(value);
		}else if(field.equals("Address")) {
			athlete.setAddress(value);
		}else if(field.equals("City")) {
			athlete.setCity(value);
		}
		athleteRepository.save(athlete);
	}
}
