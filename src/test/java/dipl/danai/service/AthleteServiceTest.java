package dipl.danai.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.WeeksReservedRepository;
import dipl.danai.app.service.AthleteService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ MockitoExtension.class}) 
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EGymApplication.class})
public class AthleteServiceTest {
		@Autowired
	    private AthleteService athleteService;

	    @MockBean
	    private AthleteRepository athleteRepository;

	    @MockBean
	    private WeeksReservedRepository weekReservedRepo;

	   @Test
	    public void testGetAthlete() {
	        String sampleEmail = "test@example.com";
	        Athletes sampleAthlete = new Athletes();
	        sampleAthlete.setEmail(sampleEmail);
	        when(athleteRepository.findByEmail(sampleEmail)).thenReturn(sampleAthlete);

	        Athletes athlete = athleteService.getAthlete(sampleEmail);
	
	        assertNotNull(athlete);
	        assertEquals(sampleEmail, athlete.getEmail());
	    }

	   @Test
	    public void testUpdateAthleteProfile() {
	        String sampleEmail = "testexample@uoi.gr";
	        Athletes sampleAthlete = new Athletes();
	        sampleAthlete.setAthlete_id(320L); 
	        sampleAthlete.setEmail(sampleEmail);
	        sampleAthlete.setAthlete_name("haha");
	        sampleAthlete.setAthlete_surname("kati");
	        when(athleteRepository.findByEmail(sampleEmail)).thenReturn(sampleAthlete);
	        String updatedFirstName = "John";
	        String updatedLastName = "Doe";
	        sampleAthlete = athleteService.updateAthleteProfile(
	                sampleEmail,
	                updatedFirstName,
	                updatedLastName,
	                "1234567890",
	                "123 Main St",
	                "City"
	        );

	        assertNotNull(sampleAthlete);
	        assertEquals(updatedFirstName, sampleAthlete.getAthlete_name());
	        assertEquals(updatedLastName, sampleAthlete.getAthlete_surname());
	    }

	    @Test
	    public void testUpdateAthletePicture() {
	        Athletes sampleAthlete = new Athletes();
	        MultipartFile sampleFile = new MockMultipartFile(
	                "sample.jpg",
	                "sample.jpg",
	                "image/jpeg",
	                "sample".getBytes()
	        );

	        athleteService.updateAthletePicture(sampleAthlete, sampleFile);

	        assertNotNull(sampleAthlete.getPicture());
	        verify(athleteRepository, times(1)).save(sampleAthlete);
	    }
}
