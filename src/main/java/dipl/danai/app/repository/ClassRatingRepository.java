package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.ClassRating;

@Repository
public interface ClassRatingRepository extends JpaRepository<ClassRating, Long> {

}
