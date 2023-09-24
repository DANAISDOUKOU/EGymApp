package dipl.danai.service;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import dipl.danai.app.EGymApplication;
import dipl.danai.app.model.User;
import dipl.danai.app.repository.UserRepository;
import dipl.danai.app.service.UserService;

@SpringBootTest
@ContextConfiguration(classes = EGymApplication.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService1;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testIsUserPresentWithExistingUser() {
    	 User user = new User();
         user.setEmail("existing@example.com");
         when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
         boolean result = userService1.isUserPresent(user) != null;
         assertTrue(result);
    }

    @Test
    public void testIsUserPresentWithNonExistingUser() {
    	User user = new User();
        user.setEmail("nonexisting@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        boolean result = userService1.isUserPresent(user) != null;
        assertTrue(result);
    }
}





