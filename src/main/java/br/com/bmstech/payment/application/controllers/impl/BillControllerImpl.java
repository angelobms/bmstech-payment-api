package br.com.bmstech.payment.application.controllers.impl;

import br.com.bmstech.payment.application.controllers.BillController;
import br.com.bmstech.payment.domain.dto.BillRequetDTO;
import br.com.bmstech.payment.domain.dto.BillResponseDTO;
import br.com.bmstech.payment.domain.entity.Bill;
import br.com.bmstech.payment.domain.mappers.BillMapper;
import br.com.bmstech.payment.infra.services.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillControllerImpl implements BillController {

    private final BillService billService;
    private final BillMapper billMapper;

    public BillResponseDTO save(@RequestBody @Valid BillRequetDTO billRequetDTO) {
        var bill = billMapper.mapDtoToEntity(billRequetDTO);
        return billMapper.mapEntityToDto(billService.create(bill));
    }

    public Page<BillResponseDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Page<Bill> bills = billService.findAll(page, size);
        return bills.map(billMapper::mapEntityToDto);
    }

    public BillResponseDTO findById(@PathVariable String id) {
        var bill = billService.findById(id);
        return billMapper.mapEntityToDto(bill);
    }

    public Page<BillResponseDTO> findbyFilter(
            @RequestParam(value = "data_vencimento", required = false) LocalDate dueDate,
            @RequestParam(value = "descricao", required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Bill> bills = billService.findByFilter(dueDate, description, page, size);
        return bills.map(billMapper::mapEntityToDto);
    }

    public BillResponseDTO update(@PathVariable String id, @RequestBody @Valid BillRequetDTO billRequetDTO) {
        var updatedBill = billService.update(id, billMapper.mapDtoToEntity(billRequetDTO));
        return billMapper.mapEntityToDto(updatedBill);
    }

    public void payBill(@PathVariable String id) {
        billService.payBill(id);
    }

    public void delete(@PathVariable String id) {
        billService.delete(id);
    }

    public List<BillResponseDTO> importBill(@RequestParam("file") MultipartFile file) {
        List<Bill> bills = billService.importBills(file);
        return bills.stream().map(billMapper::mapEntityToDto).toList();
    }
}
