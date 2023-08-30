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
    @Query(value = "INSERT INTO athletes(athlete_id, athlete_name, athlete_surname, email, Address, City, phone_number) " +
            "VALUES(:id, :name, :surname, :email, :address, :city, :phoneNumber)", nativeQuery = true)
    void insertAthlete(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname,
                @Param("email") String email, @Param("address") String address, @Param("city") String city,
                @Param("phoneNumber") String phoneNumber);

    @Query(value = "INSERT INTO gyms(gym_id, gym_name, gym_surname, email, Address, City, phone_number) " +
            "VALUES(:id, :name, :surname, :email, :address, :city, :phoneNumber)", nativeQuery = true)
    void insertGym(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname,
            @Param("email") String email, @Param("address") String address, @Param("city") String city,
            @Param("phoneNumber") String phoneNumber);

    @Query(value = "INSERT INTO instructors(instructor_id, instructor_name, instructor_surname, email, Address, City, phone_number) " +
            "VALUES(:id, :name, :surname, :email, :address, :city, :phoneNumber)", nativeQuery = true)
    void insertInstructor(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname,
                   @Param("email") String email, @Param("address") String address, @Param("city") String city,
                   @Param("phoneNumber") String phoneNumber);
}
