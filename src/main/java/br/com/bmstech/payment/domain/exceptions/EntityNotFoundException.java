package br.com.bmstech.payment.domain.exceptions;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
