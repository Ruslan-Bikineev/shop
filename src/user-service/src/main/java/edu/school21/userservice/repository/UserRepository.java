package edu.school21.userservice.repository;

import edu.school21.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM users u WHERE u.mail = :mail")
	Optional<User> findByMail(String mail);

	@Query("SELECT EXISTS (SELECT 1 FROM users u WHERE u.mail = :mail)")
	boolean isExistsByMail(String mail);

}
