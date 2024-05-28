package br.com.bmstech.payment.domain.repositories;

import br.com.bmstech.payment.domain.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    Page<Bill> findByDueDateEqualsAndDescriptionContainsIgnoreCase(LocalDate dueDate, String description, Pageable pageable);
}
