package dipl.danai.app.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
	
	@Id
	@Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long payment_id;
	
	/*
	 * @JoinColumn(name="athlete_id") public Athletes athlete_id;
	 */
	
	@Column 
	public float amount;
	
	@Column
	public Timestamp payment_time;
	
	@Column 
	public Date payment_date;

}
