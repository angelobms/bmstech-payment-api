package br.com.bmstech.payment.application.model.repositories;

import br.com.bmstech.payment.domain.entity.Bill;
import br.com.bmstech.payment.domain.enums.Situation;
import br.com.bmstech.payment.domain.repositories.BillRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class BillRepositoryTest {

    private final BillRepository billRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private static Bill bill;
    private static List<Bill> bills;

    @BeforeEach
    void setUp() {
        createBill();
        createBillList();
    }

    @Test
    @DisplayName("Should save bill")
    void shouldSaveBill() {
        var billSaved = billRepository.save(bill);
        assertThat(billSaved).isEqualTo(bill);
    }

    @Test
    @DisplayName("Should return one bill")
    void shouldReturnOneBill() {
        var billSaved = billRepository.save(bill);
        var result = billRepository.findById(billSaved.getId());
        assertThat(result).isPresent();

        var billFromGet = result.get();
        assertThat(billFromGet).isEqualTo(billSaved);
    }

    @Test
    @DisplayName("Should return all bills")
    void shouldReturnAllBills() {
        billRepository.saveAll(bills);
        List<Bill> result = billRepository.findAll();

        assertThat(result).hasSize(bills.size()).isEqualTo(bills);
    }

    @Test
    @DisplayName("Should update bill")
    void shouldUpdateBill() {
        var billSaved = billRepository.save(bill);
        var result = billRepository.findById(billSaved.getId());
        assertThat(result).isPresent();

        var billFromGet = result.get();
        assertThat(billFromGet).isEqualTo(bill);

        bill.setDescription("Bill description test 01");
        bill.setAmount(new BigDecimal("100.12"));

        var billUpdated = billRepository.saveAndFlush(bill);
        var result2 = billRepository.findById(billFromGet.getId());
        assertThat(result2).isPresent();

        var billFromGet2 = result.get();

        assertThat(billFromGet2.getDescription()).isEqualTo(billUpdated.getDescription());
        assertThat(billFromGet2.getAmount()).isEqualTo(billUpdated.getAmount());
    }

    private static void createBill() {
        bill = Bill.builder()
                .dueDate(LocalDate.of(2020, 1, 1))
                .amount(new BigDecimal("53.1"))
                .description("Bill description test 01")
                .situation(Situation.UNPAID)
                .build();
    }

    private static void createBillList() {
        bills = new ArrayList<>();
        bills.add(Bill.builder()
                .dueDate(LocalDate.of(2020, 1, 1))
                .amount(new BigDecimal("53.1"))
                .description("Bill description test 01")
                .situation(Situation.UNPAID)
                .build());
        bills.add(Bill.builder()
                .dueDate(LocalDate.of(2020, 1, 1))
                .amount(new BigDecimal("53.1"))
                .description("Bill description test 02")
                .situation(Situation.UNPAID)
                .build());
        bills.add(Bill.builder()
                .dueDate(LocalDate.of(2020, 1, 1))
                .amount(new BigDecimal("53.1"))
                .description("Bill description test 03")
                .situation(Situation.UNPAID)
                .build());
    }

    @Test
    @DisplayName("Should delete bill")
    void shouldDeleteBill() {
        var billSaved = billRepository.save(bill);
        billRepository.delete(billSaved);

        var result = billRepository.findById(billSaved.getId());
        assertThat(result).isNotPresent();
    }
}
