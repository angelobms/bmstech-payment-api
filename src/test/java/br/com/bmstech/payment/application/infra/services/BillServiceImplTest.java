package br.com.bmstech.payment.application.infra.services;

import br.com.bmstech.payment.domain.entity.Bill;
import br.com.bmstech.payment.domain.enums.Situation;
import br.com.bmstech.payment.domain.exceptions.BillNotFoundException;
import br.com.bmstech.payment.domain.repositories.BillRepository;
import br.com.bmstech.payment.infra.services.BillService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class BillServiceImplTest {

    private final BillRepository billRepository;
    private final BillService billService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeEach
    void setUp() {
        billRepository.deleteAll();
    }

    @Test
    void shouldCreateOneBillWithStatusPaid() {
        var dueDate = LocalDate.of(2020, 1, 1);
        var amount = BigDecimal.valueOf(53.15);
        var description = "Test description test";
        var situation = Situation.PAID;
        var bill = billService.create(
                Bill.builder()
                        .dueDate(dueDate)
                        .amount(amount)
                        .description(description)
                        .situation(situation)
                        .build());

        assertThat(dueDate).isEqualTo(bill.getDueDate());
        assertThat(bill.getPaymentDate()).isNull();
        assertThat(amount).isEqualTo(bill.getAmount());
        assertThat(description).isEqualTo(bill.getDescription());
        assertThat(situation).isEqualTo(bill.getSituation());
    }

    @Test
    void shouldCreateOneBillWithStatusUnpaid() {
        var dueDate = LocalDate.of(2020, 1, 1);
        var amount = BigDecimal.valueOf(53.16);
        var description = "Test description test";
        var bill = billService.create(
                Bill.builder()
                        .dueDate(dueDate)
                        .amount(amount)
                        .description(description)
                        .build());

        assertThat(dueDate).isEqualTo(bill.getDueDate());
        assertThat(bill.getPaymentDate()).isNull();
        assertThat(amount).isEqualTo(bill.getAmount());
        assertThat(description).isEqualTo(bill.getDescription());
        assertThat(bill.getSituation()).isEqualTo(Situation.UNPAID);
    }

    @Test
    void shouldFindAllBills() {
        var page = 0;
        var size = 10;
        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            bills.add(Bill.builder()
                    .dueDate(LocalDate.now().plusDays(i))
                    .amount(BigDecimal.valueOf(150 + i))
                    .description(String.format("Bill description test %d", i))
                    .build());
        }
        billRepository.saveAll(bills);

        Page<Bill> result = billService.findAll(page, size);

        assertThat(result.getContent()).hasSize(size);
        assertThat(result.getNumber()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);
    }

    @Test
    void shouldFindByIdWithSuccess() {
        var billSaved = billRepository.save(Bill.builder()
                .dueDate(LocalDate.now())
                .amount(BigDecimal.valueOf(10.12))
                .description("Bill description test")
                .situation(Situation.UNPAID)
                .build());
        var uuid = String.valueOf(billSaved.getId());
        var result = billService.findById(uuid);

        assertThat(billSaved.getId()).isEqualTo(result.getId());
    }

    @Test
    void shouldFindByIdWithNotFoundException() {
        var uuid = String.valueOf(UUID.randomUUID());
        assertThatThrownBy(() -> billService.findById(uuid)).isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void shouldFindBillsByFilter() {
        var dueDate = LocalDate.now();
        var description = "Bill description test";
        var page = 0;
        var size = 10;

        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            bills.add(Bill.builder()
                    .dueDate(LocalDate.now())
                    .amount(BigDecimal.valueOf(40 + i))
                    .description(String.format(description.concat(" %d"), i))
                    .situation(Situation.UNPAID)
                    .build());
        }
        billRepository.saveAll(bills);

        Page<Bill> result = billService.findByFilter(dueDate, "Test", page, size);

        assertThat(result.getNumber()).isEqualTo(page);
        assertThat(result.getNumberOfElements()).isEqualTo(size);
        assertThat(result.getContent().stream().allMatch(bill -> bill.getDescription().contains(description))).isTrue();
        assertThat(result.getContent().stream().allMatch(bill -> bill.getDueDate().equals(dueDate))).isTrue();
    }

    @Test
    void shouldUpdateBillWithSuccess() {
        var bill = billRepository.save(Bill.builder()
                .dueDate(LocalDate.now())
                .amount(BigDecimal.valueOf(10.12))
                .description("Bill description test")
                .situation(Situation.UNPAID)
                .build());

        bill.setDescription("Bill description updated");
        bill.setAmount(BigDecimal.valueOf(40.12));

        var billUpdated = billService.update(String.valueOf(bill.getId()), bill);
        var result = billRepository.findById(billUpdated.getId());
        assertThat(result).isPresent();

        var billFormGet = result.get();
        assertThat(billFormGet.getDescription()).isEqualTo("Bill description updated");
        assertThat(billUpdated.getAmount()).isEqualTo(BigDecimal.valueOf(40.12));
    }

    @Test
    void shouldUpdateWithNotFoundException() {
        var uuid = String.valueOf(UUID.randomUUID());
        assertThatThrownBy(() -> billService.update(uuid, null)).isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void shouldPayBillWithSuccess() {
        var bill = billRepository.save(Bill.builder()
                .dueDate(LocalDate.now())
                .amount(BigDecimal.valueOf(10.12))
                .description("Bill description test")
                .situation(Situation.UNPAID)
                .build());

        billService.payBill(String.valueOf(bill.getId()));

        var billPaid = billRepository.findById(bill.getId());

        assertThat(billPaid).isPresent();
        assertThat(billPaid.get().getSituation()).isEqualTo(Situation.PAID);
    }

    @Test
    void shouldPayBillWithNotFoundException() {
        var uuid = String.valueOf(UUID.randomUUID());
        assertThatThrownBy(() -> billService.payBill(uuid)).isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void shouldDeleteBillWithSuccess() {
        var bill = billRepository.save(Bill.builder()
                .dueDate(LocalDate.now())
                .amount(BigDecimal.valueOf(10.12))
                .description("Bill description test")
                .situation(Situation.UNPAID)
                .build());

        billService.delete(String.valueOf(bill.getId()));

        var billDeleted = billRepository.findById(bill.getId());
        assertThat(billDeleted).isNotPresent();
    }

    @Test
    void shouldDeleteBillWithNotFoundException() {
        var uuid = UUID.randomUUID().toString();
        assertThatThrownBy(() -> billService.delete(uuid)).isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void shouldImportBills() {
        var file = getMockMultipartFile();
        List<Bill> billsImported = billService.importBills(file);

        assertThat(billsImported).hasSize(12);

        var bill1 = billsImported.getFirst();
        assertThat(bill1.getDueDate()).isEqualTo(LocalDate.of(2024, 1, 5));
        assertThat(bill1.getPaymentDate()).isEqualTo(LocalDate.of(2024, 1, 4));
        assertThat(bill1.getAmount()).isEqualTo(BigDecimal.valueOf(284.25));
        assertThat(bill1.getDescription()).isEqualTo("Bill description test 01");
        assertThat(bill1.getSituation()).isEqualTo(Situation.PAID);

        var bill2 = billsImported.getLast();
        assertThat(bill2.getDueDate()).isEqualTo(LocalDate.of(2024, 12, 5));
        assertThat(bill2.getPaymentDate()).isNull();
        assertThat(bill2.getAmount()).isEqualTo(BigDecimal.valueOf(284.25));
        assertThat(bill2.getDescription()).isEqualTo("Bill description test 12");
        assertThat(bill2.getSituation()).isEqualTo(Situation.UNPAID);

        List<Bill> bills = billRepository.findAll();
        assertThat(bills).hasSize(12);
    }

    private static @NotNull MockMultipartFile getMockMultipartFile() {
        String csvContent = """
                             data_vencimento,data_pagamento,valor,descricao,situacao
                             2024-01-05,2024-01-04,284.25,Bill description test 01,PAID
                             2024-02-05,2024-01-04,284.25,Bill description test 02,PAID
                             2024-03-05,2024-01-04,284.25,Bill description test 03,PAID
                             2024-04-05,2024-01-04,284.25,Bill description test 04,PAID
                             2024-05-05,2024-01-04,284.25,Bill description test 05,PAID
                             2024-06-05,,284.25,Bill description test 06,
                             2024-07-05,,284.25,Bill description test 07,
                             2024-08-05,,284.25,Bill description test 08,
                             2024-09-05,,284.25,Bill description test 09,
                             2024-10-05,,284.25,Bill description test 10,
                             2024-11-05,,284.25,Bill description test 11,
                             2024-12-05,,284.25,Bill description test 12,
                             """;
        return new MockMultipartFile("file", "bills.csv", "text/csv", csvContent.getBytes());
    }
}
