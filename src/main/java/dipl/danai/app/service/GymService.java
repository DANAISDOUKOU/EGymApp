package dipl.danai.app.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
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
	
	@Autowired
	NominatimService geonameService;
	

	public void saveGym(Gym gym) {
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
		if(field.equals("name")) {
			gym.setGym_name(value);
		}else if(field.equals("surname")) {
			gym.setGym_surname(value);
		}else if(field.equals("number")) {
			gym.setPhoneNumber(value);
		}else if(field.equals("address")) {
			gym.setAddress(value);
			 Map<String, Double> coordinates = geonameService.getCoordinatesForAddressInCity(value, gym.getCity());
			    if (coordinates != null) {
			        gym.setLatitude(coordinates.get("latitude"));
			        gym.setLongitude(coordinates.get("longitude"));
			    }
		}else if(field.equals("city")) {
			gym.setCity(value);
		}
		gymRepository.save(gym);
	}


	public List<Gym> searchGyms(String searchBy, String query,String sortBy,String address) {
	    List<Gym> allGyms = gymRepository.findAll();
	   getAllGymsWithCoordinates();
	    List<Gym> searchResults ;
	    if ("bestRating".equalsIgnoreCase(sortBy)) {
	        searchResults = allGyms.stream()
	                .sorted(Comparator.comparing(Gym::getAverageRating).reversed())
	                .collect(Collectors.toList());
	    } else if ("address".equalsIgnoreCase(sortBy)) {
	        searchResults = allGyms.stream()
	                .sorted(Comparator.comparingDouble(gym -> calculateDistance(address, gym.getLatitude(), gym.getLongitude())))
	                .collect(Collectors.toList());
	    } else {
	    searchResults = allGyms.stream()
	            .filter(gym -> (searchBy == null || searchBy.trim().isEmpty() || matchesSearchCriteria(gym, searchBy, query)))
	            .collect(Collectors.toList());
	    }
	    return searchResults;
	}

	private boolean matchesSearchCriteria(Gym gym, String searchBy, String query) {
	    query = query.trim().toLowerCase();

	    switch (searchBy) {
	        case "name":
	            return gym.getGym_name().toLowerCase().contains(query);
	        case "workoutType":
	            final String queryLowerCase = query; 
	            return gym.getGymWorkouts().stream().anyMatch(workout -> workout.getName().toLowerCase().contains(queryLowerCase));
	        case "city":
	            return gym.getCity().toLowerCase().contains(query);
	        default:
	            return false;
	    }
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
	

	
	public List<Gym> getAllGymsWithCoordinates() {
        List<Gym> allGyms = gymRepository.findAll();

        for (Gym gym : allGyms) {
            Map<String, Double> coordinates = geonameService.getCoordinatesForAddressInCity(gym.getAddress(), gym.getCity());
            if (coordinates != null) {
                gym.setLatitude(coordinates.get("latitude"));
                gym.setLongitude(coordinates.get("longitude"));
                gymRepository.save(gym);
            }
        }

        return allGyms;
    }
	
	private double calculateDistance(String yourAddress, double gymLatitude, double gymLongitude) {
	    Map<String, Double> yourCoordinates = geonameService.getCoordinatesForAddressInCity(yourAddress, null);

	    if (yourCoordinates != null) {
	        double lat1 = yourCoordinates.get("latitude");
	        double lon1 = yourCoordinates.get("longitude");
	        double lat2 = gymLatitude;
	        double lon2 = gymLongitude;

	        // Implement the Haversine formula to calculate the distance
	        double radius = 6371; // Earth's radius in kilometers
	        double dLat = Math.toRadians(lat2 - lat1);
	        double dLon = Math.toRadians(lon2 - lon1);
	        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
	                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        return radius * c;
	    }

	    return Double.MAX_VALUE; // Return a very large value if coordinates cannot be determined
	}
}
