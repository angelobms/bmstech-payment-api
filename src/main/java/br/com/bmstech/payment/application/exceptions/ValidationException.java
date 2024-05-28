package br.com.bmstech.payment.application.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public class ValidationException extends RuntimeException {

    private BindingResult bindingResult;
}
