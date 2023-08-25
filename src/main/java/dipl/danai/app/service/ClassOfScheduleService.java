package dipl.danai.app.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.repository.ClassOfScheduleRepository;

@Service 
public class ClassOfScheduleService {
	@Autowired
	ClassOfScheduleRepository classOfScheduleRepository;
	
	
	public void save(ClassOfSchedule c) {
		classOfScheduleRepository.save(c);
	}

	public int getAvaliablePositions(ClassOfSchedule c) {
		int maxCapacity=c.getCapacity();
		int registeredParticipants=c.getParticipants().size();
		return maxCapacity-registeredParticipants;
	}
	
	public void addParicipant(Athletes athlete,ClassOfSchedule classOfSchedule){
		List<Athletes> participants=classOfSchedule.getParticipants();
		participants.add(athlete);
		classOfSchedule.setParticipants(participants);
		
	}
	
	  public ClassOfSchedule getClassScheduleDetails(Long classOfScheduleId) {
	        return classOfScheduleRepository.findById(classOfScheduleId).orElse(null);
	  }
	  
	public void removeParticipant(Athletes athlete,ClassOfSchedule classOfSchedule){
		List<Athletes> participants=classOfSchedule.getParticipants();
		participants.remove(athlete);
		classOfSchedule.setParticipants(participants);
			
	}
}
