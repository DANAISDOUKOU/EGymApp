package dipl.danai.app.service;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Payment;
import dipl.danai.app.repository.PaymentRepository;

@Service
public class PaymentService {
	  @Autowired
	  private EmailService emailService;

	   @Autowired
	   private PaymentRepository paymentRepository;

	   public void makePayment(Athletes athlete, Gym gym, double amount) {
	        Payment payment = new Payment();
	        payment.setAthlete(athlete);
	        payment.setGym(gym);
	        payment.setAmount(amount);
	        payment.setPayment_time(new Timestamp(System.currentTimeMillis()));
	        payment.setPayment_date(new Date(System.currentTimeMillis()));
	        paymentRepository.save(payment);
	        String subject = "Payment Received";
	        String message = "Hello Gym Owner,\n\n"
	                         + "Athlete "+athlete.getAthlete_name()+" "+athlete.getAthlete_surname() +" has made a payment of " + amount + " to your gym.";

	        emailService.sendEmail(gym.getEmail(), subject, message);
	    }

}
