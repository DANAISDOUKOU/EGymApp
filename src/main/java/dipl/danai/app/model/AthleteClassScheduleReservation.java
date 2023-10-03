package dipl.danai.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "class_schedule_reservations")
public class AthleteClassScheduleReservation {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "class_of_schedule_id")
	    private ClassOfSchedule classOfSchedule;

	    @ManyToOne
	    @JoinColumn(name = "athlete_id")
	    private Athletes athlete;

	    @Column(name = "reserved_week")
	    private Integer reservedWeek;


	    public AthleteClassScheduleReservation(Athletes a, ClassOfSchedule classOfSchedule2, Integer weeksToReserve) {
	    	this.athlete=a;
	    	this.classOfSchedule=classOfSchedule2;
	    	this.reservedWeek=weeksToReserve;
	    }

		public ClassOfSchedule getClassOfSchedule() {
	        return classOfSchedule;
	    }

	    public void setClassOfSchedule(ClassOfSchedule classOfSchedule) {
	        this.classOfSchedule = classOfSchedule;
	    }

	    public Athletes getAthlete() {
	        return athlete;
	    }

	    public void setAthlete(Athletes athlete) {
	        this.athlete = athlete;
	    }

	    public Integer getReservedWeek() {
	        return reservedWeek;
	    }

	    public void setReservedWeek(Integer reservedWeek) {
	        this.reservedWeek = reservedWeek;
	    }
}
