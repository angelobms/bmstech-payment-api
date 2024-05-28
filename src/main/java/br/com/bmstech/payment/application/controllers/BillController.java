package br.com.bmstech.payment.application.controllers;

import br.com.bmstech.payment.domain.dto.BillRequetDTO;
import br.com.bmstech.payment.domain.dto.BillResponseDTO;
import br.com.bmstech.payment.domain.entity.Bill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface BillController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BillResponseDTO save(@RequestBody @Valid BillRequetDTO billRequetDTO);

    @Operation(summary = "Get all bills")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the bill",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Bill.class)) }),
            @ApiResponse(responseCode = "404", description = "Bills not found", content = @Content) })
    @GetMapping
    Page<BillResponseDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Get a bill by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the bills",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Bill.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bill not found", content = @Content) })
    @GetMapping("/{id}")
    BillResponseDTO findById(@PathVariable String id);

    @GetMapping("/filter")
    Page<BillResponseDTO> findbyFilter(
            @RequestParam(value = "data_vencimento", required = false) LocalDate dueDate,
            @RequestParam(value = "descricao", required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @PutMapping("/{id}")
    BillResponseDTO update(@PathVariable String id, @RequestBody @Valid BillRequetDTO billRequetDTO);

    @PutMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void payBill(@PathVariable String id);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String id) ;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.OK)
    List<BillResponseDTO> importBill(@RequestParam("file") MultipartFile file);
}
