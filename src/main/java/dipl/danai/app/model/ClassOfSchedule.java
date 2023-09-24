package dipl.danai.app.model;

import java.sql.Time;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	@Column
	private Time time_start;

	@Column
	private Time time_end;

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

	public Time getTime_start() {
	    return time_start;
	}

	public void setTime_start(Time time_start) {
	    this.time_start = time_start;
	}

	public Time getTime_end() {
	    return time_end;
	}

	public void setTime_end(Time time_end) {
	    this.time_end = time_end;
	}

}
