package br.com.bmstech.payment.infra.services.impl;

import br.com.bmstech.payment.domain.entity.Bill;
import br.com.bmstech.payment.domain.enums.Situation;
import br.com.bmstech.payment.domain.exceptions.BillNotFoundException;
import br.com.bmstech.payment.domain.repositories.BillRepository;
import br.com.bmstech.payment.infra.services.BillService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    @Transactional
    public Bill create(Bill bill) {
        if (Objects.isNull(bill.getSituation())) {
            bill.setSituation(Situation.UNPAID);
        }
        return billRepository.save(bill);
    }

    public Page<Bill> findAll(int page, int size) {
        return billRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Bill findById(String id) {
        var uuid = UUID.fromString(id);
        return billRepository.findById(uuid)
                .orElseThrow(() -> new BillNotFoundException(uuid));
    }

    public Page<Bill> findByFilter(LocalDate dueDate, String description, int page, int size) {
        return billRepository.findByDueDateEqualsAndDescriptionContainsIgnoreCase(dueDate, description, PageRequest.of(page, size));
    }

    @Transactional
    public Bill update(String id, Bill bill) {
        var uuid = UUID.fromString(id);
        if (!billRepository.existsById(uuid)) throw new BillNotFoundException(uuid);
        bill.setId(uuid);
        return billRepository.save(bill);
    }

    @Transactional
    public void payBill(String id) {
        var uuid = UUID.fromString(id);
        Optional<Bill> bill = billRepository.findById(uuid);
        if (bill.isEmpty()) throw new BillNotFoundException(uuid);
        bill.get().pay();
    }

    @Transactional
    public void delete(String id) {
        var uuid = UUID.fromString(id);
        if (!billRepository.existsById(uuid)) throw new BillNotFoundException(uuid);
        billRepository.deleteById(uuid);
    }

    @Transactional
    public List<Bill> importBills(MultipartFile file) {
        List<Bill> bills = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(bufferedReader);

            for (CSVRecord row : records) {
                var dueDate = row.get("data_vencimento").trim();
                var paymentDate = row.get("data_pagamento").trim();
                var amount = row.get("valor").trim();
                var description = row.get("descricao").trim();
                var situation = row.get("situacao").trim();

                Bill bill = new Bill();
                bill.setDueDate(LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE));
                bill.setPaymentDate(!paymentDate.isEmpty() ? LocalDate.parse(paymentDate, DateTimeFormatter.ISO_DATE) : null);
                bill.setAmount(new BigDecimal(amount));
                bill.setDescription(description);
                bill.setSituation(!situation.isEmpty() ? Situation.valueOf(situation) : Situation.UNPAID);
                bills.add(bill);
            }
            billRepository.saveAll(bills);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bills;
    }
}
