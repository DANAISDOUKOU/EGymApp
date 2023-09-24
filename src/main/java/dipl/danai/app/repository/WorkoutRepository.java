package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Workout;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long>{
		@Query(value="SELECT w FROM Workout w WHERE name=?1")
		public Workout findByName(String name);

}
