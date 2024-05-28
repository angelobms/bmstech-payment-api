package br.com.bmstech.payment.infra.services;

import br.com.bmstech.payment.domain.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public interface BillService {

    Bill create(Bill bill);
    Page<Bill> findAll(int page, int size);
    Bill findById(String id);
    Page<Bill> findByFilter(LocalDate dueDate, String description, int page, int size);
    Bill update(String id, Bill bill);
    void payBill(String id);
    void delete(String id);
    List<Bill> importBills(MultipartFile file);

}
