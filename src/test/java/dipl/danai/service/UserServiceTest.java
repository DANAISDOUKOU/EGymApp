package dipl.danai.service;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Date;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.User;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.UserRepository;
import dipl.danai.app.service.NominatimService;
import dipl.danai.app.service.UserService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AthleteRepository athleteRepository;

    @MockBean
    private GymRepository gymRepository;

    @MockBean
    private InstructorRepository instructorRepository;

    @MockBean
    private NominatimService nominatimService;
    
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testIsUserPresentWithExistingUser() {
    	 User user = new User();
         user.setEmail("existing@example.com");
         when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
         boolean result = userService.isUserPresent(user) != null;
         assertTrue(result);
    }

    @Test
    public void testIsUserPresentWithNonExistingUser() {
    	User user = new User();
        user.setEmail("nonexisting@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        boolean result = userService.isUserPresent(user) != null;
        assertTrue(result);
    }
    
    @Test
    public void testSaveUser() {
        User user = new User();
        user.setPassword("password123");
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        
        userService.saveUser(user);
        assertEquals(user.getPassword(),"hashedPassword");
        verify(userRepository).save(user);
    }

    @Test
    public void testLoadUserByUsername() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
    }

    @Test
    public void testGenerateResetToken() {
        String resetToken = userService.generateResetToken();

        assertNotNull(resetToken);
        assertTrue(resetToken.length() > 0);
    }

    @Test
    public void testValidateResetToken() {
        String resetToken = "validToken";
        User user = new User();
        user.setResetToken(resetToken);
        user.setResetTokenExpiryDate(new Date(System.currentTimeMillis() + 3600 * 1000));

        when(userRepository.findByResetToken(resetToken)).thenReturn(user);

        User result = userService.validateResetToken(resetToken);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    public void testValidateResetTokenExpired() {
        String resetToken = "expiredToken";
        User user = new User();
        user.setResetToken(resetToken);
        user.setResetTokenExpiryDate(new Date(System.currentTimeMillis() - 3600 * 1000));

        when(userRepository.findByResetToken(resetToken)).thenReturn(user);

        User result = userService.validateResetToken(resetToken);

        assertNull(result);
    }

    @Test
    public void testIsTokenExpired() {
        Date expiryDate = new Date(System.currentTimeMillis() + 3600 * 1000);
        assertFalse(userService.isTokenExpired(expiryDate));

        expiryDate = new Date(System.currentTimeMillis() - 3600 * 1000);
        assertTrue(userService.isTokenExpired(expiryDate));
    }


    @Test
    public void testGetUser() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.getUser(email);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    public void testUpdateUserProfile() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        String firstName = "John";
        String lastName = "Doe";
        String phoneNumber = "1234567890";
        String address = "123 Main St";
        String city = "Sample City";

        User updatedUser = userService.updateUserProfile(email, firstName, lastName, phoneNumber, address, city);

        assertNotNull(updatedUser);
        assertEquals(firstName, updatedUser.getFirstName());
        assertEquals(lastName, updatedUser.getLastName());
        assertEquals(phoneNumber, updatedUser.getPhoneNumber());
        assertEquals(address, updatedUser.getAddress());
        assertEquals(city, updatedUser.getCity());

        verify(userRepository).save(user);
    }

    @Test
    public void testSaveProfilePicture() throws IOException {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        MultipartFile profilePicture = mock(MultipartFile.class);
        byte[] profilePictureBytes = new byte[1];

        when(profilePicture.getBytes()).thenReturn(profilePictureBytes);
        when(userRepository.findByEmail(email)).thenReturn(user);

       userService.saveProfilePicture(user, profilePicture);

        verify(profilePicture).getBytes();
        verify(userRepository).save(user);
    }

    @Test
    public void testInsertAthlete() {
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        String email = "athlete@example.com";
        String address = "123 Main St";
        String city = "Sample City";
        String phoneNumber = "1234567890";
        byte[] profilePictureBytes = new byte[1];

        userService.insertAthlete(id, name, surname, email, address, city, phoneNumber, profilePictureBytes);

        ArgumentCaptor<Athletes> athleteCaptor = ArgumentCaptor.forClass(Athletes.class);
        verify(athleteRepository).save(athleteCaptor.capture());

        Athletes athlete = athleteCaptor.getValue();
        assertEquals(id, athlete.getAthlete_id());
        assertEquals(name, athlete.getAthlete_name());
        assertEquals(surname, athlete.getAthlete_surname());
        assertEquals(email, athlete.getEmail());
        assertEquals(address, athlete.getAddress());
        assertEquals(city, athlete.getCity());
        assertEquals(phoneNumber, athlete.getPhoneNumber());
        assertArrayEquals(profilePictureBytes, athlete.getPicture());
    }

}





