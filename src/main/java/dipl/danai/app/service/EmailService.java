package dipl.danai.app.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dipl.danai.app.model.Athletes;
import dipl.danai.app.model.ClassOfSchedule;
import dipl.danai.app.model.Gym;
import dipl.danai.app.model.Schedule;

@Service
public class EmailService {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private GymService gymService;
	private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    
    @Transactional
    @Scheduled(fixedRate = 86400000) 
    public void sendLessonReminders() {
        LocalDate currentDate = LocalDate.now();
        List<Gym> gyms = gymService.getGyms();
        
        for (Gym gym : gyms) {
            try (Session session = sessionFactory.openSession()) {
                Schedule schedule = gymService.getScheduleFromGymForDateAndGymId(Date.valueOf(currentDate), gym);

                if (schedule != null) {
                    for (ClassOfSchedule c : schedule.getScheduleClasses()) {
                        List<Athletes> participants = c.getParticipants();

                        for (Athletes a : participants) {
                            sendEmail(a.getEmail(), "Notification for Classes Today At " + gym.getGym_name(), "You have this class today " + c.getWorkout() + " at " + c.getTime_start());
                        }
                    }
                }
            }
        }
    }
}
