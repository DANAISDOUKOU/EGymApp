package dipl.danai.app.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.repository.ScheduleRepository;

@Service
public class ScheduleService {
	@Autowired
	ScheduleRepository scheduleRepository;

	List<ClassOfSchedule> classes=new ArrayList<>();


	public void saveClass(ClassOfSchedule class1) {
		classes.add(class1);
	}

	public void saveSchedule(Schedule program) {
		program.setScheduleClasses(classes);
		scheduleRepository.save(program);
		classes=new ArrayList<>();
	}

	public boolean isRoomOccupied(Room selectedRoom, Time valueOf, Time valueOf2,Schedule schedule) {
		for(ClassOfSchedule c: schedule.getScheduleClasses()) {
			if(c.getTime_start().equals(valueOf)&&c.getTime_end().equals(valueOf2)&&c.getRoom().getRoomId().equals(selectedRoom.getRoomId())){
				return true;
			}
		}
		return false;
	}

	public boolean isInstructorOccupied(Instructor selectedInstructor, Time valueOf, Time valueOf2,Schedule schedule) {
		for(ClassOfSchedule c: schedule.getScheduleClasses()) {
			if(c.getTime_start().equals(valueOf)&&c.getTime_end().equals(valueOf2) && c.getInstructor().getInstructor_id().equals(selectedInstructor.getInstructor_id())) {
				return true;
			}
		}
		return false;
	}

	public Schedule getScheduleById(Long id) {
		Schedule schedule=scheduleRepository.findById(id).orElse(null);
		return schedule;
	}

	public void saveUpdatedSchedule(Schedule schedule) {
		scheduleRepository.save(schedule);
	}

	public boolean isRoomOccupiedInClasses(Room selectedRoom, Time valueOf, Time valueOf2) {
		for(ClassOfSchedule c: classes) {
			if(c.getTime_start().equals(valueOf)&&c.getTime_end().equals(valueOf2)&&c.getRoom().getRoomId().equals(selectedRoom.getRoomId())){
				return true;
			}
		}
		return false;
	}

	public boolean isInstructorOccupiedInClasses(Instructor selectedInstructor, Time valueOf, Time valueOf2) {
		for(ClassOfSchedule c: classes) {
			if(c.getTime_start().equals(valueOf)&&c.getTime_end().equals(valueOf2) && c.getInstructor().getInstructor_id().equals(selectedInstructor.getInstructor_id())) {
				return true;
			}
		}
		return false;
	}
}
