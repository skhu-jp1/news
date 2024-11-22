package se.sowl.jigeumdomain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sowl.jigeumdomain.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
}
