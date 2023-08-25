package dipl.danai.app.service;


import dipl.danai.app.model.User;
import dipl.danai.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

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
      
        
        System.out.println("existingUserEmail.isPresent() - "+existingUserEmail!=null);
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
		}else if(field.equals("Phone")) {
			user.setLastName(value);
		}else if(field.equals("Address")) {
			user.setLastName(value);
		}
		userRepository.save(user);
	}
}
