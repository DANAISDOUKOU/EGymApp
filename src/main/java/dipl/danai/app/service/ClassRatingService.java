package dipl.danai.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.ClassRating;
import dipl.danai.app.repository.ClassRatingRepository;

@Service
public class ClassRatingService {
		@Autowired
		ClassRatingRepository classRatingRepo;
		
		public void saveClassRating(ClassRating classRating) {
			classRatingRepo.save(classRating);
		}
		
}
