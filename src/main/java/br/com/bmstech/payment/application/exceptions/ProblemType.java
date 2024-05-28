package br.com.bmstech.payment.application.exceptions;

import lombok.Getter;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Getter
public enum ProblemType {

    SYSTEM_ERROR("System error", "/system-error"),
    INVALID_PARAMETER("Invalid parameter", "/invalid-parameter"),
    INCOMPREHENSIBLE_MESSAGE("Incomprehensible message","/incomprehensible-message"),
    RESOURCE_NOT_FOUND("Resource not found", "/resource-not-found"),
    ENTITY_IN_USE("Entity in use", "/entity-in-use"),
    ERROR_BUSINESS("Violation of business rule", "/erro-business"),
    INVALID_DATA("Invalid data", "/invalid-data");

    private final String title;
    private final String uri;

    ProblemType(String title, String path) {
        this.title = title;
        this.uri = "https://bmstech.com.br" + path;
    }
}
