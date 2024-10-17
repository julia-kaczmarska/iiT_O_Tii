package back.repository;

import back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); //logowanie

    Optional<User> findByUserId(Long userId); //pobieranie kategorii

    boolean existsByEmail(String email);


}