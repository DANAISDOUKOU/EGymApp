package dipl.danai.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import dipl.danai.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.resetToken = :resetToken")
    User findByResetToken(@Param("resetToken") String resetToken);
}
