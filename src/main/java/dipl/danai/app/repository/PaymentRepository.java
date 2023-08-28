package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dipl.danai.app.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
