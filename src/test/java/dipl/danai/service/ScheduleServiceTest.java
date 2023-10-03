package dipl.danai.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.repository.ScheduleRepository;
import dipl.danai.app.service.ScheduleService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ MockitoExtension.class}) 
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EGymApplication.class})
public class ScheduleServiceTest {
	@MockBean
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;
    
    @Mock
    private ClassOfSchedule classOfSchedule1;
    @Mock
    private ClassOfSchedule classOfSchedule2;
   
    
    @Test
    public void testIsInstructorOccupied() {     

        Instructor instructor1 = new Instructor();
        instructor1.setInstructor_id(1L);
        Instructor instructor2 = new Instructor();
        instructor2.setInstructor_id(2L);

        when(classOfSchedule1.getTime_start()).thenReturn(Time.valueOf("09:00:00"));
        when(classOfSchedule1.getTime_end()).thenReturn(Time.valueOf("10:00:00"));
        when(classOfSchedule1.getInstructor()).thenReturn(instructor1);

        when(classOfSchedule2.getTime_start()).thenReturn(Time.valueOf("11:00:00"));
        when(classOfSchedule2.getTime_end()).thenReturn(Time.valueOf("12:00:00"));
        when(classOfSchedule2.getInstructor()).thenReturn(instructor2);
        Schedule schedule = new Schedule();
        schedule.setScheduleClasses(List.of(classOfSchedule1, classOfSchedule2));

        boolean isOccupied = scheduleService.isInstructorOccupied(classOfSchedule1.getInstructor(), Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), schedule);
        assertTrue(isOccupied);

        isOccupied = scheduleService.isInstructorOccupied(classOfSchedule1.getInstructor(), Time.valueOf("11:00:00"), Time.valueOf("12:00:00"), schedule);
        assertFalse(isOccupied);

        Instructor differentInstructor = new Instructor();
        differentInstructor.setInstructor_id(3L);
        isOccupied = scheduleService.isInstructorOccupied(differentInstructor, Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), schedule);
        assertFalse(isOccupied);
    }
    
    @Test
    public void testIsRoomOccupied() {     

        Room room1 = new Room();
        room1.setRoomId(1L);
        Room room2 = new Room();
        room2.setRoomId(2L);

        when(classOfSchedule1.getTime_start()).thenReturn(Time.valueOf("09:00:00"));
        when(classOfSchedule1.getTime_end()).thenReturn(Time.valueOf("10:00:00"));
        when(classOfSchedule1.getRoom()).thenReturn(room1);

        when(classOfSchedule2.getTime_start()).thenReturn(Time.valueOf("11:00:00"));
        when(classOfSchedule2.getTime_end()).thenReturn(Time.valueOf("12:00:00"));
        when(classOfSchedule2.getRoom()).thenReturn(room2);
        Schedule schedule = new Schedule();
        schedule.setScheduleClasses(List.of(classOfSchedule1, classOfSchedule2));
        boolean isOccupied = scheduleService.isRoomOccupied(classOfSchedule1.getRoom(), Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), schedule);
        assertTrue(isOccupied);
        isOccupied = scheduleService.isRoomOccupied(classOfSchedule1.getRoom(), Time.valueOf("11:00:00"), Time.valueOf("12:00:00"), schedule);
        assertFalse(isOccupied);
        Room room3 = new Room();
        room3.setRoomId(3L);
        isOccupied = scheduleService.isRoomOccupied(room3, Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), schedule);
        assertFalse(isOccupied);
    }
}
