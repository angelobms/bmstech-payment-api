package br.com.bmstech.payment.infra.security;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
public class TokenException extends RuntimeException {
    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
