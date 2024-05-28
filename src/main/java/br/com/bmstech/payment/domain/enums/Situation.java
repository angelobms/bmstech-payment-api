package br.com.bmstech.payment.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum Situation {

    @JsonProperty("PAGO") PAID("Pago"),
    @JsonProperty("NAO_PAGO") UNPAID("Não pago");

    private final String description;
}
