package dipl.danai.app.model;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "classes")
public class ClassOfSchedule {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long classOfScheduleId;
	
	@ManyToOne
	@JoinColumn(name="workout_id")
	private Workout workout;
	
	@ManyToOne
	@JoinColumn(name="time_id")
	private Times time;
	
	@ManyToOne
	@JoinColumn(name="instructor_id")
	private Instructor instructor;
	
	@ManyToOne
	@JoinColumn(name="roomId")
	private Room room;
	
	@Column
	private int capacity;
	
	@Column
	private Boolean is_canceled;
	
	@ManyToMany
	@JoinTable(name="athletes_classes",joinColumns=@JoinColumn(name="classOfScheduleId"),inverseJoinColumns=@JoinColumn(name="athlete_id"))
	private List<Athletes> participants;
	
	@ManyToMany(mappedBy = "ScheduleClasses")
	private List<Schedule> schedules;
	
	public Workout getWorkout() {
		return workout;
	}

	public void setWorkout(Workout workout) {
		this.workout = workout;
	}

	public Times getTime() {
		return time;
	}

	public void setTime(Times time) {
		this.time = time;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}

	public List<Athletes> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Athletes> participants) {
		this.participants = participants;
	}

	public Long getClassOfScheduleId() {
		return classOfScheduleId;
	}

	public void setClassOfScheduleId(Long classOfScheduleId) {
		this.classOfScheduleId = classOfScheduleId;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public List<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}

	public Boolean isIs_canceled() {
		return is_canceled;
	}

	public void setIs_canceled(Boolean is_canceled) {
		this.is_canceled = is_canceled;
	}
	
	
	
}
