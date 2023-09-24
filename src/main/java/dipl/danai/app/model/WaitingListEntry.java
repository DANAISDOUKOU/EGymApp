package dipl.danai.app.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "waiting_list")
public class WaitingListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classOfScheduleId")
    private ClassOfSchedule classOfSchedule;

    @ManyToOne
    @JoinColumn(name = "athleteId")
    private Athletes athlete;

    @Column
    private LocalDateTime joinedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}



}
