package br.com.bmstech.payment.domain.exceptions;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public class EntityInUseException extends BusinessException {

    public EntityInUseException(String message) {
        super(message);
    }
}
