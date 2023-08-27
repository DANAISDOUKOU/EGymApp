package dipl.danai.app.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "membershipType")
public class MembershipType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long membership_type_id;
	
	@Column
	private String membership_type_name;
	
	@Column
	private String membership_period;
	
	@Column 
	private float membership_amount;
	
	@ManyToMany(mappedBy="memberships")
	private Set<Athletes> athletes;
	
	@ManyToOne
	@JoinColumn(name = "gym_id")
	 private Gym gym;

	
	public Long getMembership_type_id() {
		return membership_type_id;
	}

	public void setMembership_type_id(Long membership_type_id) {
		this.membership_type_id = membership_type_id;
	}

	public String getMembership_type_name() {
		return membership_type_name;
	}

	public void setMembership_type_name(String membership_type_name) {
		this.membership_type_name = membership_type_name;
	}

	public String getMembership_period() {
		return membership_period;
	}

	public void setMembership_period(String membership_period) {
		this.membership_period = membership_period;
	}

	public float getMembership_amount() {
		return membership_amount;
	}

	public void setMembership_amount(float membership_amount) {
		this.membership_amount = membership_amount;
	}

	public Set<Athletes> getAthletes() {
		return athletes;
	}

	public void setAthletes(Set<Athletes> athletes) {
		this.athletes = athletes;
	}

	public Gym getGym() {
		return gym;
	}

	public void setGym(Gym gym) {
		this.gym = gym;
	}
}
