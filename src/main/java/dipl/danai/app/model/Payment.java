package dipl.danai.app.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
	
	@Id
	@Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long payment_id;
	
	
	@ManyToOne
	@JoinColumn(name = "athlete_id")
	private Athletes athlete;
	 
	@ManyToOne
	@JoinColumn(name = "gym_id")
	private Gym gym;
	 
	@Column 
	private double amount;
	
	@Column
	private Timestamp payment_time;
	
	@Column 
	private Date payment_date;

	public Long getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(Long payment_id) {
		this.payment_id = payment_id;
	}

	public Athletes getAthlete() {
		return athlete;
	}

	public void setAthlete(Athletes athlete) {
		this.athlete = athlete;
	}

	public Gym getGym() {
		return gym;
	}

	public void setGym(Gym gym) {
		this.gym = gym;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Timestamp getPayment_time() {
		return payment_time;
	}

	public void setPayment_time(Timestamp payment_time) {
		this.payment_time = payment_time;
	}

	public Date getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}
	
	

}
