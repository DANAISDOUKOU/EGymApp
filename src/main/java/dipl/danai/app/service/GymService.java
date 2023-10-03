package dipl.danai.app.service;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Instructor;
import dipl.danai.app.model.Membership;
import dipl.danai.app.model.MembershipType;
import dipl.danai.app.model.Room;
import dipl.danai.app.model.Schedule;
import dipl.danai.app.model.Workout;
import dipl.danai.app.repository.AthleteRepository;
import dipl.danai.app.repository.GymRepository;
import dipl.danai.app.repository.MembershipRepository;
import dipl.danai.app.repository.RoomRepository;

@Service
public class GymService {
	@Autowired
	private AthleteRepository athleteRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private GymRepository gymRepository;
	@Autowired
	private MembershipRepository membershipRepository;
	@Autowired
	private NominatimService nominatimService;

	@Transactional
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
	
	@Transactional
	public Gym updateGymProfile(String email, String firstName, String lastName, String phoneNumber, String address,
			String city) {
		Gym gym=gymRepository.findByEmail(email);

		gym.setGym_name(firstName);
		gym.setGym_surname(lastName);
		gym.setPhoneNumber(phoneNumber);
		gym.setAddress(address);
		Map<String, Double> coordinates = nominatimService.getCoordinatesForAddressInCity(address, gym.getCity());
		    if (coordinates != null) {
		    	gym.setLatitude(coordinates.get("latitude"));
		    	gym.setLongitude(coordinates.get("longitude"));
			}
		gym.setCity(city);
		
		gymRepository.save(gym);
		return gym;
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
            Map<String, Double> coordinates = nominatimService.getCoordinatesForAddressInCity(gym.getAddress(), gym.getCity());
            if (coordinates != null) {
                gym.setLatitude(coordinates.get("latitude"));
                gym.setLongitude(coordinates.get("longitude"));
                gymRepository.save(gym);
            }
        }
        return allGyms;
    }

	private double calculateDistance(String yourAddress, double gymLatitude, double gymLongitude) {
	    Map<String, Double> yourCoordinates = nominatimService.getCoordinatesForAddressInCity(yourAddress, null);
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
	    return Double.MAX_VALUE;
	}

	public List<Athletes> searchMembers(String searchTerm,Set<Athletes> members) {
		  List<Athletes> matchingAthletes = new ArrayList<>();
		  for (Athletes athlete : members) {
		      if (athlete.getAthlete_name().contains(searchTerm)) {
		          matchingAthletes.add(athlete);
		      }
		  }
		  return matchingAthletes;
	}

	public void updateGymPicture(Gym gym,MultipartFile file) {
		try {
			gym.setPicture(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		gymRepository.save(gym);
	}

	public List<Workout> searchWokrouts(String searchTerm, List<Workout> avaliableWorkouts) {
		List<Workout> workouts=new ArrayList<>();
		for(Workout w:avaliableWorkouts) {
			if(w.getName().contains(searchTerm)) {
				workouts.add(w);
			}
		}
		return workouts;
	}

	public List<Instructor> searchInstructors(String searchTerm, List<Instructor> avaliableInstructor) {
		List<Instructor> instructors=new ArrayList<>();		
		for(Instructor i:avaliableInstructor) {
			System.out.println(searchTerm+" "+i.getInstructor_name());
			if(i.getInstructor_name().contains(searchTerm)) {
					System.out.println("bhkaaa");
				instructors.add(i);
			}
		}
		return instructors;
	}
	
	public boolean shouldUseMembershipTypesAsOffers(Long gymId) {
        Gym gym = gymRepository.findById(gymId).orElse(null);
        if (gym != null) {
            return gym.isUseMembershipTypesAsOffers();
        }
        return false; 
    }

	public void updateUseMembershipTypesAsOffers(Gym gym) {
		gym.setUseMembershipTypesAsOffers(gym.isUseMembershipTypesAsOffers());		
		gymRepository.save(gym);
	}
	
	public Schedule getScheduleFromGymForDateAndGymId(Date workOutDate, Gym gym) {
        return gymRepository.findScheduleFromGymForDateAndGymId(workOutDate, gym);
    }
	
}
