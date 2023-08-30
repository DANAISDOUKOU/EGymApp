package dipl.danai.app.service;


import dipl.danai.app.model.User;
import dipl.danai.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

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
    
	public void setValue(String value,String field,User user) {
		if(field.equals("firstName")) {
			user.setFirstName(value);
		}else if(field.equals("lastName")) {
			user.setLastName(value);
		}else if(field.equals("PhoneNumber")) {
			user.setPhoneNumber(value);
		}else if(field.equals("Address")) {
			user.setAddress(value);
		}else if(field.equals("City")) {
			user.setCity(value);
		}
		userRepository.save(user);
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
              String phoneNumber) {
         userRepository.insertAthlete(id, name, surname, email, address, city, phoneNumber);
	 }
	 
	 public void insertGym( Long id,String name, String surname,
             String email,  String address,  String city,
             String phoneNumber) {
        userRepository.insertGym(id, name, surname, email, address, city, phoneNumber);
	 }
	 public void insertInstructor( Long id,String name, String surname,
             String email,  String address,  String city,
             String phoneNumber) {
        userRepository.insertInstructor(id, name, surname, email, address, city, phoneNumber);
	 }
}
