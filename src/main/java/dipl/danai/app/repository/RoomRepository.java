package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Room;
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	@Query("SELECT r FROM Room r WHERE r.roomName = ?1 AND r.gym.gym_id = ?2")
	Room findByNameAndGymId(String name, Long gymId);

}
