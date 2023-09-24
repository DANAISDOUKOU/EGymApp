package dipl.danai.app.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOccurrence;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.WaitingListEntry;
import dipl.danai.app.repository.ClassOccurrenceRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.RoomRepository;
import dipl.danai.app.repository.WaitingListEntryRepository;
import dipl.danai.app.repository.WorkoutRepository;

@Service
public class ClassOfScheduleService {
	@Autowired
	ClassOfScheduleRepository classOfScheduleRepository;

	@Autowired
	ClassOccurrenceRepository classOccurenceRepo;

	@Autowired
	WaitingListEntryRepository waitingListEntryRepository;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	WorkoutRepository workoutRepository;

	@Autowired
	InstructorRepository instructorRepository;

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

	public ClassOfSchedule getClassOfScheduleById(Long id) {
	    ClassOfSchedule classOfSchedule = classOfScheduleRepository.findById(id).orElse(null);
	    return classOfSchedule;
	}

	public void checkIfCanceled(Schedule p,ClassOfSchedule classOfSchedule) {
		if(classOccurenceRepo.findByScheduleIdAndClassOfScheduleId(p.getSchedule_id(),classOfSchedule.getClassOfScheduleId())!=null) {
			classOfSchedule.setIs_canceled(true);
			classOfScheduleRepository.save(classOfSchedule);
		}
		else {
			classOfSchedule.setIs_canceled(false);
			classOfScheduleRepository.save(classOfSchedule);
		}
	}

	public int findPositionInWitingList(ClassOfSchedule classOfSchedule, Athletes athlete) {
		int entriesBeforeAthlete = waitingListEntryRepository.countBeforeAthleteJoinedAt(classOfSchedule, athlete);
		return entriesBeforeAthlete;
	}

	public List<WaitingListEntry> getlist(ClassOfSchedule classOfSchedule){
		return  waitingListEntryRepository.findByClassOfSchedule(classOfSchedule);
	}

	public void deleteFromWaitingList(WaitingListEntry athlete) {
		waitingListEntryRepository.delete(athlete);
	}

	public ClassOccurrence getClassOccurrence(Long scheduleId, Long classId) {
		return classOccurenceRepo.findByScheduleIdAndClassOfScheduleId(scheduleId, classId);
	}

	public void saveClassOccurrence(ClassOccurrence classOcc) {
		classOccurenceRepo.save(classOcc);
	}

	public List<ClassOccurrence> findAll(){
		return classOccurenceRepo.findAll();
	}

	public void deleteOccurrence(ClassOccurrence o) {
		 classOccurenceRepo.delete(o);
	}

	public void saveWaitingListEntry(WaitingListEntry entry) {
		 waitingListEntryRepository.save(entry);

	}
}
