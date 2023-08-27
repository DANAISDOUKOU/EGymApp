package dipl.danai.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.ScheduleRepository;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.MembershipRepository;
import dipl.danai.app.repository.RoomRepository;

@Service
public class GymService {
	@Autowired
	AthleteRepository athleteRepository;
	
	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
	GymRepository gymRepository;
	
	@Autowired
	ScheduleRepository scheduleRepository;
	
	@Autowired
	MembershipRepository membershipRepository;
	
	Collection<Schedule> programs=new ArrayList<>();
	List<Workout> workouts=new ArrayList<>();
	Set<Instructor> instructors=new HashSet<>();

	public void saveProgram(Schedule program) {
		programs.add(program);
	}

	public void saveGym(Gym gym) {
		gym.setGymInstructors(instructors);
		gym.setGymWorkouts(workouts);
		gym.setGymSchedules(programs);
		gymRepository.save(gym);	
	}

	public void addMembershipToGym(Long gymId, MembershipType membership) {
		Gym gym=gymRepository.findById(gymId).orElseThrow(() -> new IllegalArgumentException("Invalid gym ID"));
		if(gym.getGym_memberships()==null) {
			gym.setGym_memberships(new ArrayList<>());
		}
		gym.getGym_memberships().add(membership);
		gymRepository.save(gym);
	}

	public boolean checkIfAlreadyMember(Long gymId, String email) {
		 Set<Athletes> gymMembers=gymRepository.findMembers(gymId);
		 Athletes athlete=athleteRepository.findByEmail(email);
		 Long athlete_id=athlete.getAthlete_id();
		 boolean alreadyMember=false;
	     for(Athletes member:gymMembers) {
	    	 if(member.getAthlete_id().equals(athlete_id)) {
	    		 alreadyMember=true;
	    		 break;
	    	 }
	     }
		return alreadyMember;
	}

	public boolean checkIfHasSpecificMembership(Long gymId, String email,boolean alreadyMember) {
		Athletes athlete=athleteRepository.findByEmail(email);
		if(alreadyMember) {
			 Membership membership = membershipRepository.findByAthleteAndMembershipType_Gym(gymId, athlete);
		     if(membership!=null) {
		    	 return true;
		     }   
			 return false;
		}
		return false;
		}
	
	public void addRoomToGym(Gym gym, Room room) {
		gym.getRooms().add(room);
		gymRepository.save(gym);
		room.setGym(gym);
		roomRepository.save(room);
	}

	public void setValue(String value,String field, Gym gym) {
		if(field.equals("gym_name")) {
			gym.setGym_name(value);
		}else if(field.equals("gym_surname")) {
			gym.setGym_surname(value);
		}else if(field.equals("PhoneNumber")) {
			gym.setPhoneNumber(value);
		}else if(field.equals("Address")) {
			gym.setAddress(value);
		}else if(field.equals("City")) {
			gym.setCity(value);
		}
		gymRepository.save(gym);
	}

	public void addWorkout(Workout workout) {
		workouts.add(workout);
	}

	public void addInstructor(Instructor instructor) {
		instructors.add(instructor);
	}

	public List<Gym> searchGyms(String name, String address, String workoutType, Integer bestRating, String city) {
		 List<Gym> allGyms = gymRepository.findAll(); 
	   
		 List<Gym> searchResults = allGyms.stream()
	                .filter(gym -> (name == null || name.trim().isEmpty() ||gym.getGym_name().trim().equalsIgnoreCase(name.trim())))
	                .filter(gym -> (workoutType == null || workoutType.trim().isEmpty() || gym.getGymWorkouts().stream()
                    .anyMatch(workout -> workout.getName().equalsIgnoreCase(workoutType.trim()))))
	                //.filter(gym -> (address == null || gym.getAddress().contains(address)))
	                //.filter(gym -> (workoutType == null || gym.getGymWorkouts().stream()
                   // .anyMatch(workout -> workout.getName().contains(workoutType))))
	                .filter(gym -> (city==null|| city.trim().isEmpty()||gym.getCity().trim().equalsIgnoreCase(city.trim())))
	                .filter(gym -> (bestRating == null || gym.getAverageRating() >= bestRating))
	                .collect(Collectors.toList());
		
	   return searchResults;
	}
	
	public List<Gym> getGyms(){
		List<Gym> gyms=gymRepository.findAll();
		return gyms;
	}
	
	public Gym getGymById(Long id) {
		 Gym gym = gymRepository.findById(id).orElse(null);
		 return gym;
	}
	
	public Gym getGymByEmail(String email) {
		Gym gym=gymRepository.findByEmail(email);
		return gym;

	}
	
	public Set<Athletes> findMembers(Long id){
		Set<Athletes> members=gymRepository.findMembers(id);
		return members;
	}
	
	public void saveUpdatedGym(Gym gym) {
		gymRepository.save(gym);
	}
	
	public MembershipType findExistingMembership(Long gymId,Long athleteId) {
		Athletes athlete=athleteRepository.findById(athleteId).orElse(null);
		Membership existingMembership=membershipRepository.findByAthleteAndMembershipType_Gym(gymId,athlete);
		MembershipType existingMembershipType=existingMembership.getMembershipType();
		return existingMembershipType;
	}
	
	public Room getRoomByName(String name,Long gymId) {
		return  roomRepository.findByNameAndGymId(name,gymId);
	}
	
	public Room getRoomById(Long id) {
		return  roomRepository.findById(id).orElse(null);
	}

	public List<Gym> getGymsByCity(String city) {
		 List<Gym> allGyms = gymRepository.findAll();
		 List<Gym> searchResults = allGyms.stream().filter(gym -> (city==null|| city.trim().isEmpty()||gym.getCity().trim().equalsIgnoreCase(city.trim()))).collect(Collectors.toList());
		 return searchResults;
	}
}
