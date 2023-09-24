package dipl.danai.app.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.User;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.InstructorRepository;
import dipl.danai.app.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private NominatimService nominatimService;
    
    private byte[] temporaryProfilePictureBytes;

    public void setTemporaryProfilePictureBytes(byte[] profilePictureBytes) {
    	System.out.println(profilePictureBytes);
        this.temporaryProfilePictureBytes = profilePictureBytes;
    }

    public byte[] getTemporaryProfilePictureBytes() {
        return temporaryProfilePictureBytes;
    }

    public void clearTemporaryProfilePictureBytes() {
        this.temporaryProfilePictureBytes = null;
    }
    public void saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    @Transactional
    public List<Object> isUserPresent(User user) {
        boolean userExists = false;
        String message = null;
        User existingUserEmail = userRepository.findByEmail(user.getEmail());
        if(existingUserEmail!=null){
            userExists = true;
            message = "Email Already Present!";
        }
        return Arrays.asList(userExists, message);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user=userRepository.findByEmail(email);
       if(user==null) {
    	   throw new UsernameNotFoundException("User not found with this email");
       }
       return user;
    }


	 public String generateResetToken() {
		   return UUID.randomUUID().toString();
	 }

	 public User validateResetToken(String resetToken) {
	    User user = userRepository.findByResetToken(resetToken);
	    if (user != null && !isTokenExpired(user.getResetTokenExpiryDate())) {
	        return user;
	    }
	    return null;
	  }

	 public boolean isTokenExpired(Date expiryDate) {
		 return expiryDate.before(new Date());
	 }

	 public Date calculateExpiryDate() {
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.HOUR, 1);
		 return calendar.getTime();
	}

	 public User getUser(String email) {
		 User user=userRepository.findByEmail(email);
		 return user;
	 }

	 public void updateSaveUser(User user) {
		 userRepository.save(user);
	 }

	 public void insertAthlete( Long id,String name, String surname,
              String email,  String address,  String city,
              String phoneNumber, byte[] profilePictureBytes) {
		 Athletes athlete = new Athletes(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
		 athleteRepository.save(athlete);
	 }

	 public void insertGym( Long id,String name, String surname,
             String email,  String address,  String city,
             String phoneNumber,byte[] profilePictureBytes) {

        Gym gym=new Gym(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
        gymRepository.save(gym);
        Map<String, Double> coordinates = nominatimService.getCoordinatesForAddressInCity(address, city);
	    if (coordinates != null) {
	        gym.setLatitude(coordinates.get("latitude"));
	        gym.setLongitude(coordinates.get("longitude"));
	    }
	    gymRepository.save(gym);
	 }
	 public void insertInstructor( Long id,String name, String surname,
             String email,  String address,  String city,
             String phoneNumber,byte[] profilePictureBytes) {
        Instructor instructor=new Instructor(id, name, surname, email, address, city, phoneNumber,profilePictureBytes);
        instructorRepository.save(instructor);
	 }
	 public User updateUserProfile(String email, String firstName, String lastName, String phoneNumber, String address,
				String city) {

		    User user = userRepository.findByEmail(email);
		    if (user != null) {
		        user.setFirstName(firstName);
		        user.setLastName(lastName);
		        user.setPhoneNumber(phoneNumber);
		        user.setAddress(address);
		        user.setCity(city);
		        userRepository.save(user);
		        return user;
		    } else {
		        return null;
		    }
		}

	 public void saveProfilePicture(User user, MultipartFile profilePicture) {
	        try {
	            byte[] profilePictureBytes = profilePicture.getBytes();
	            System.out.println("ppppp"+profilePictureBytes);
	            user.setProfilePicture(profilePictureBytes);
	            userRepository.save(user);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("Failed to save profile picture: " + e.getMessage());
	        }
	    }
}
