package br.com.bmstech.payment.domain.exceptions;

import java.util.UUID;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public class BillNotFoundException extends EntityNotFoundException {

    private static final String BILL_NOT_FOUND_MSG = "There is no bill register with the code %s.";

    public BillNotFoundException(String message) {
        super(message);
    }

    public BillNotFoundException(UUID billId) {
        this(String.format(BILL_NOT_FOUND_MSG, billId));
    }
}
