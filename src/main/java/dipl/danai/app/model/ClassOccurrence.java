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
@Table(name = "class_occurrences")
public class ClassOccurrence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long occurenceId;

    @ManyToOne
    @JoinColumn(name = "class_schedule_id")
    private ClassOfSchedule classOfSchedule;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(name = "is_canceled")
    private boolean canceled;

	public Long getId() {
		return occurenceId;
	}

	public void setId(Long id) {
		this.occurenceId = id;
	}

	public ClassOfSchedule getClassOfSchedule() {
		return classOfSchedule;
	}

	public void setClassOfSchedule(ClassOfSchedule classOfSchedule) {
		this.classOfSchedule = classOfSchedule;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

    
}

