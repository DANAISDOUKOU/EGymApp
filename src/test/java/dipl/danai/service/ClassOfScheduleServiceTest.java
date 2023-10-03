package dipl.danai.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOccurrence;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.WaitingListEntry;
import dipl.danai.app.repository.ClassOccurrenceRepository;
import dipl.danai.app.repository.ClassOfScheduleRepository;
import dipl.danai.app.repository.WaitingListEntryRepository;
import dipl.danai.app.service.ClassOfScheduleService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ MockitoExtension.class}) 
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EGymApplication.class})
public class ClassOfScheduleServiceTest {
	
	 @Autowired
	    private ClassOfScheduleService classOfScheduleService;
	 	@MockBean
	  	private ClassOfScheduleRepository classOfScheduleRepository;

	    @MockBean
	    private ClassOccurrenceRepository classOccurrenceRepository;

	    @MockBean
	    private WaitingListEntryRepository waitingListEntryRepository;
	    
	    @Test
	    public void testGetClassScheduleDetails() {
	        Long classOfScheduleId = 1L;
	        ClassOfSchedule sampleClassOfSchedule = new ClassOfSchedule();
	        sampleClassOfSchedule.setClassOfScheduleId(classOfScheduleId);
	        when(classOfScheduleRepository.findById(classOfScheduleId)).thenReturn(Optional.of(sampleClassOfSchedule));

	        ClassOfScheduleService classOfScheduleService = new ClassOfScheduleService();
	        classOfScheduleService.setClassOfScheduleRepository(classOfScheduleRepository);
	        ClassOfSchedule result = classOfScheduleService.getClassScheduleDetails(classOfScheduleId);

	        assertNotNull(result);
	        assertEquals(classOfScheduleId, result.getClassOfScheduleId());
	    }

	    @Test
	    public void testFindPositionInWaitingList() {
	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        Athletes athlete = new Athletes();
	        List<WaitingListEntry> waitingList = new ArrayList<>();

	        WaitingListEntry entry1 = new WaitingListEntry();
	        entry1.setAthlete(athlete);
	        entry1.setJoinedAt(LocalDateTime.now());

	        WaitingListEntry entry2 = new WaitingListEntry();
	        entry2.setAthlete(athlete);
	        entry1.setJoinedAt(LocalDateTime.now());

	        waitingList.add(entry1);
	        waitingList.add(entry2);

	        when(waitingListEntryRepository.findByClassOfSchedule(classOfSchedule)).thenReturn(waitingList);

	        ClassOfScheduleService classOfScheduleService = new ClassOfScheduleService();
	       classOfScheduleService.setWaitingListEntryRepository(waitingListEntryRepository);
	        int position = classOfScheduleService.findPositionInWitingList(classOfSchedule, athlete);

	        assertEquals(0, position);
	    }
	    
	    @Test
	    public void testAddParticipant() {
	        Athletes athlete = new Athletes();
	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        List<Athletes> participants = new ArrayList<>();
	        classOfSchedule.setParticipants(participants);

	        ClassOfScheduleService classOfScheduleService = Mockito.spy(new ClassOfScheduleService());

	        // when(classOfScheduleService.getClassOfScheduleById(any(Long.class))).thenReturn(classOfSchedule);

	        classOfScheduleService.addParicipant(athlete, classOfSchedule);

	        List<Athletes> updatedParticipants = classOfSchedule.getParticipants();
	        assertNotNull(updatedParticipants);
	        assertEquals(1, updatedParticipants.size());
	        assertTrue(updatedParticipants.contains(athlete));
	    }

	    @Test
	    public void testRemoveParticipant() {
	        Athletes athlete = new Athletes();
	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        List<Athletes> participants = new ArrayList<>();
	        participants.add(athlete);
	        classOfSchedule.setParticipants(participants);

	        ClassOfScheduleService classOfScheduleService = Mockito.spy(new ClassOfScheduleService());

	        // when(classOfScheduleService.getClassOfScheduleById(any(Long.class))).thenReturn(classOfSchedule);

	        classOfScheduleService.removeParticipant(athlete, classOfSchedule);

	        List<Athletes> updatedParticipants = classOfSchedule.getParticipants();
	        assertNotNull(updatedParticipants);
	        assertTrue(updatedParticipants.isEmpty());
	    }
	
	    @Test
	    public void testCheckIfCanceledWithClassOccurrence() {
	        Schedule schedule = new Schedule();
	        ClassOfSchedule classOfSchedule = new ClassOfSchedule();
	        classOfSchedule.setClassOfScheduleId(1L);

	        ClassOccurrence classOccurrence = new ClassOccurrence();
	        classOccurrence.setSchedule(schedule);
	        classOccurrence.setClassOfSchedule(classOfSchedule);

	        ClassOfScheduleService classOfScheduleService = Mockito.spy(new ClassOfScheduleService());

	        ClassOccurrenceRepository classOccurrenceRepo = mock(ClassOccurrenceRepository.class);
	        doAnswer(invocation -> {
	            classOfSchedule.setIs_canceled(false);
	            return null; 
	        }).when(classOfScheduleRepository).save(classOfSchedule);
	        classOfScheduleService.classOccurenceRepo = classOccurrenceRepo;

	        ClassOfScheduleRepository classOfScheduleRepository = mock(ClassOfScheduleRepository.class);
	        when(classOfScheduleRepository.save(classOfSchedule)).thenReturn(classOfSchedule);
	        
	        classOfScheduleService.classOfScheduleRepository = classOfScheduleRepository;

	        classOfScheduleService.checkIfCanceled(schedule, classOfSchedule);

	        assertTrue(!classOfSchedule.isIs_canceled());
	    } 
	    
}
 