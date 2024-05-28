package br.com.bmstech.payment.domain.dto;

import br.com.bmstech.payment.domain.enums.Situation;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillRequetDTO {

    @JsonProperty("data_pagamento")
    private LocalDate paymentDate;

    @NotNull(message = "Due date cannot be null")
    @JsonProperty("data_vencimento")
    private LocalDate dueDate;

    @NotNull(message = "Amount cannot be null")
    @JsonProperty("valor")
    private BigDecimal amount;

    @NotBlank(message = "Description cannot be null")
    @JsonProperty("descricao")
    private String description;

    @NotNull(message = "Situation cannot be null")
    @JsonProperty("situacao")
    private Situation situation;
}
