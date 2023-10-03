package dipl.danai.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.MembershipRepository;
import dipl.danai.app.repository.RoomRepository;
import dipl.danai.app.service.GymService;
import dipl.danai.app.service.NominatimService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ MockitoExtension.class}) 
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EGymApplication.class})
public class GymServiceTest {
	 @MockBean
	 private GymRepository gymRepository;

	 @MockBean
	 private AthleteRepository athleteRepository;

	 @MockBean
	 private MembershipRepository membershipRepository;
	 @MockBean
	 private RoomRepository roomRepository;

	 @InjectMocks
	 private GymService gymService;
	 
	 @MockBean
	 private NominatimService nominatimService;
	 @Test
	    public void testCheckIfAlreadyMember() {
	        // Arrange
	        Long gymId = 1L;
	        String email = "test@example.com";
	        Gym gym = new Gym();
	        Athletes athlete = new Athletes();
	        athlete.setAthlete_id(1L);
	        Set<Athletes> gymMembers = Set.of(athlete);

	        when(gymRepository.findMembers(gymId)).thenReturn(gymMembers);
	        when(athleteRepository.findByEmail(email)).thenReturn(athlete);

	        // Act
	        boolean alreadyMember = gymService.checkIfAlreadyMember(gymId, email);

	        // Assert
	        assertTrue(alreadyMember);
	    }
	 
	 @Test
	    public void testCheckIfHasSpecificMembership() {
	        // Arrange
	        Long gymId = 1L;
	        String email = "test@example.com";
	        Athletes athlete = new Athletes();
	        athlete.setAthlete_id(1L);
	        Gym gym = new Gym();
	        gym.setGym_id(gymId);
	        gym.setGym_memberships(new ArrayList<>());
	        MembershipType m=new MembershipType();
	        Membership membershipType = new Membership();
	        membershipType.setMembershipType(m);
	        membershipType.setAthlete(athlete);

	        when(athleteRepository.findByEmail(email)).thenReturn(athlete);
	        when(membershipRepository.findByAthleteAndMembershipType_Gym(gymId, athlete)).thenReturn(membershipType);

	        boolean hasMembership = gymService.checkIfHasSpecificMembership(gymId, email, true);

	        assertTrue(hasMembership);
	    }

	    @Test
	    public void testAddRoomToGym() {
	        Gym gym = new Gym();
	        Room room = new Room();
	        gym.setRooms(new ArrayList());
	        gymService.addRoomToGym(gym, room);
	        when(roomRepository.save(room)).thenReturn(room);
	        assertEquals(1, gym.getRooms().size());
	        assertSame(gym, room.getGym());
	    }
	    
	    @Test
	    public void testSearchGymsByBestRating() {
	        // Arrange
	        String searchBy = "name";
	        String query = "gymName";
	        String sortBy = "bestRating";
	        String address = "123 Main St";

	        Gym gym1 = new Gym();
	        gym1.setAverageRating(4.5);
	        Gym gym2 = new Gym();
	        gym2.setAverageRating(3.5);
	        List<Gym> allGyms = List.of(gym1, gym2);

	        when(gymRepository.findAll()).thenReturn(allGyms);

	        // Act
	        List<Gym> searchResults = gymService.searchGyms(searchBy, query, sortBy, address);

	        // Assert
	        assertEquals(2, searchResults.size());
	        assertSame(gym1, searchResults.get(0)); // gym1 has higher rating, so it should come first
	        assertSame(gym2, searchResults.get(1));
	    }

	    @Test
	    public void testSearchGymsByAddress() {
	        // Arrange
	        String searchBy = "name";
	        String query = "gymName";
	        String sortBy = "address";
	        String address = "123 Main St";

	        Gym gym1 = new Gym();
	        gym1.setLatitude(40.0);
	        gym1.setLongitude(-75.0);
	        Gym gym2 = new Gym();
	        gym2.setLatitude(42.0);
	        gym2.setLongitude(-73.0);
	        List<Gym> allGyms = List.of(gym1, gym2);
	        when(nominatimService.getCoordinatesForAddressInCity(any(), any())).thenReturn(Map.of("latitude", 40.0, "longitude", -75.0));
	        when(gymRepository.findAll()).thenReturn(allGyms);

	        // Act
	        List<Gym> searchResults = gymService.searchGyms(searchBy, query, sortBy, address);

	        // Assert
	        assertEquals(2, searchResults.size());
	        assertSame(gym2, searchResults.get(1)); // gym2 is closer to the given address
	        assertSame(gym1, searchResults.get(0));
	    }

	    @Test
	    public void testMatchesSearchCriteriaByName() {
	        // Arrange
	        Gym gym = new Gym();
	        gym.setGym_name("Gym Name");
	        String searchBy = "name";
	        String query = "name";

	        // Act
	        boolean matchesCriteria = gymService.matchesSearchCriteria(gym, searchBy, query);

	        // Assert
	        assertTrue(matchesCriteria);
	    }

	    @Test
	    public void testMatchesSearchCriteriaByWorkoutType() {
	        // Arrange
	        Gym gym = new Gym();
	        gym.setGymWorkouts(new ArrayList());
	        Workout workout1 = new Workout();
	        workout1.setName("Yoga");
	        Workout workout2 = new Workout();
	        workout2.setName("Pilates");
	        gym.getGymWorkouts().add(workout1);
	        gym.getGymWorkouts().add(workout2);
	        String searchBy = "workoutType";
	        String query = "Yoga";

	        // Act
	        boolean matchesCriteria = gymService.matchesSearchCriteria(gym, searchBy, query);

	        // Assert
	        assertTrue(matchesCriteria);
	    }

	    @Test
	    public void testMatchesSearchCriteriaByCity() {
	        // Arrange
	        Gym gym = new Gym();
	        gym.setCity("New York");
	        String searchBy = "city";
	        String query = "New York";

	        // Act
	        boolean matchesCriteria = gymService.matchesSearchCriteria(gym, searchBy, query);

	        // Assert
	        assertTrue(matchesCriteria);
	    }

	    @Test
	    public void testMatchesSearchCriteriaByInvalidSearchBy() {
	        // Arrange
	        Gym gym = new Gym();
	        String searchBy = "invalid";
	        String query = "query";

	        // Act
	        boolean matchesCriteria = gymService.matchesSearchCriteria(gym, searchBy, query);

	        // Assert
	        assertFalse(matchesCriteria);
	    }

}
