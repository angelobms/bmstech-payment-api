package br.com.bmstech.payment.domain.entity;

import br.com.bmstech.payment.domain.enums.Situation;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Entity
@Table(name = "bills", schema = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate paymentDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private Situation situation;

    public void pay() {
        setSituation(Situation.PAID);
    }
}
