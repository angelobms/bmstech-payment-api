package br.com.bmstech.payment.domain.repositories;

import br.com.bmstech.payment.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
