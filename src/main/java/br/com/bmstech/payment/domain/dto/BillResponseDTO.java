package br.com.bmstech.payment.domain.dto;

import br.com.bmstech.payment.domain.enums.Situation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillResponseDTO(
        UUID id,
        @JsonProperty("data_pagamento") LocalDate paymentDate,
        @JsonProperty("data_vencimento") LocalDate dueDate,
        @JsonProperty("valor") BigDecimal amount,
        @JsonProperty("descricao") String description,
        @JsonProperty("situacao") Situation situation
) { }
