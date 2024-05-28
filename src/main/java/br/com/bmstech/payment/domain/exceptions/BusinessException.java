package br.com.bmstech.payment.domain.exceptions;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
