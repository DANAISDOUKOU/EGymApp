package dipl.danai.app.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dipl.danai.app.repository.ScheduleRepository;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;

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
	public boolean isRoomOccupied(Room selectedRoom, Time valueOf, Time valueOf2) {
		for(ClassOfSchedule c: classes) {
			System.out.println(c.getTime().getTime_start()+" "+valueOf+" end "+c.getTime().getTime_end()+" "+valueOf2+" ");
			System.out.println(c.getRoom().getRoomId()+" "+selectedRoom.getRoomId());
			if(c.getTime().getTime_start().equals(valueOf)&&c.getTime().getTime_end().equals(valueOf2)&&c.getRoom().getRoomId().equals(selectedRoom.getRoomId())){
				return true;
			}
		}
		return false;
	}

	public boolean isInstructorOccupied(Instructor selectedInstructor, Time valueOf, Time valueOf2) {
		for(ClassOfSchedule c: classes) {
			System.out.println("Instructorrrr");
			System.out.println(c.getTime().getTime_start()+" "+valueOf+" end "+c.getTime().getTime_end()+" "+valueOf2+" ");
			System.out.println(c.getInstructor().getInstructor_id()+" "+selectedInstructor.getInstructor_id());
			if(c.getTime().getTime_start().equals(valueOf)&&c.getTime().getTime_end().equals(valueOf2) && c.getInstructor().getInstructor_id().equals(selectedInstructor.getInstructor_id())) {
				return true;
			}
		}
		return false;
	}

}
