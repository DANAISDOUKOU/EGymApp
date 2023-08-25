package dipl.danai.app.model;

import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "times")
public class Times{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long time_id;
	
	@Column(unique=true)
	private Time time_start;
	
	@Column(unique=true)
	private Time time_end;
	
	public Long getTime_id() {
		return time_id;
	}

	public void setTime_id(Long time_id) {
		this.time_id = time_id;
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
