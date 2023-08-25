package dipl.danai.app.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Schedule")
public class Schedule {
	
	@Id
	@Column
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long schedule_id;

	@ManyToMany(mappedBy="gymSchedules")
	private Collection<Gym> gyms;
	
	@Column
	private Date work_out_date;
		
	@ManyToMany
	@JoinTable(name="schedule_classes",joinColumns=@JoinColumn(name="schedule_id"),inverseJoinColumns=@JoinColumn(name="classOfSchedule_id"))
	private List<ClassOfSchedule> ScheduleClasses=new ArrayList<>();
		
	
	public Date getWork_out_date() {
		return work_out_date;
	}

	public void setWork_out_date(Date work_out_date) {
		this.work_out_date = work_out_date;
	}
	
	public Collection<Gym> getGyms() {
		return gyms;
	}

	public void setGyms(Collection<Gym> gyms) {
		this.gyms = gyms;
	}

	public Long getSchedule_id() {
		return schedule_id;
	}

	public void setSchedule_id(Long schedule_id) {
		this.schedule_id = schedule_id;
	}

	public List<ClassOfSchedule> getScheduleClasses() {
		return ScheduleClasses;
	}

	public void setScheduleClasses(List<ClassOfSchedule> scheduleClasses) {
		ScheduleClasses = scheduleClasses;
	}
	
	
	

}
