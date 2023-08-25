package dipl.danai.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.WaitingListEntry;

@Repository
public interface WaitingListEntryRepository extends JpaRepository<WaitingListEntry,Long>{
	@Query("SELECT COUNT(entry) FROM WaitingListEntry entry " +
		       "WHERE entry.classOfSchedule = :classOfSchedule " +
		       "AND entry.joinedAt <= (SELECT joinedAt FROM WaitingListEntry WHERE athlete = :athlete)")
	 int countBeforeAthleteJoinedAt(@Param("classOfSchedule") ClassOfSchedule classOfSchedule,
	                                   @Param("athlete") Athletes athlete);
	
	 @Query("SELECT entry FROM WaitingListEntry entry " +
	           "WHERE entry.classOfSchedule = :classOfSchedule")
	 List<WaitingListEntry> findByClassOfSchedule(@Param("classOfSchedule") ClassOfSchedule classOfSchedule);
}
