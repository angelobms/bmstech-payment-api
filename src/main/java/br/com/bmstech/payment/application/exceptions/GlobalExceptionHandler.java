package br.com.bmstech.payment.application.exceptions;

import br.com.bmstech.payment.domain.exceptions.BusinessException;
import br.com.bmstech.payment.domain.exceptions.EntityInUseException;
import br.com.bmstech.payment.domain.exceptions.EntityNotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.bmstech.payment.application.exceptions.ProblemType.ENTITY_IN_USE;
import static br.com.bmstech.payment.application.exceptions.ProblemType.ERROR_BUSINESS;
import static br.com.bmstech.payment.application.exceptions.ProblemType.INCOMPREHENSIBLE_MESSAGE;
import static br.com.bmstech.payment.application.exceptions.ProblemType.INVALID_DATA;
import static br.com.bmstech.payment.application.exceptions.ProblemType.INVALID_PARAMETER;
import static br.com.bmstech.payment.application.exceptions.ProblemType.RESOURCE_NOT_FOUND;
import static br.com.bmstech.payment.application.exceptions.ProblemType.SYSTEM_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String SYSTEM_ERROR_MSG = """
            An unexpected internal system error has occurred. Please try again and if the problem persists,
            contact the system administrator.
            """;

    private final MessageSource messageSource;

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotSupportedException ex,
                                                                      HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(status).headers(headers).build();
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                         WebRequest request) {
        return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                    HttpHeaders headers, HttpStatus status, WebRequest request) {
        var detail = String.format(""" 
                URL parameter '%s' received value '%s', which is of an invalid type. Please enter a value compatible with type %s.
                """, ex.getName(), ex.getValue(), (Objects.nonNull(ex.getRequiredType())) ? ex.getRequiredType().getSimpleName() : Strings.EMPTY);

        var problem = createProblemBuilder(status, INVALID_PARAMETER, detail)
                .userMessage(SYSTEM_ERROR_MSG)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        var rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        } else if (rootCause instanceof PropertyBindingException) {
            return handlePropertyBindingException((PropertyBindingException) rootCause, headers, status, request);
        }

        var detail = "The request body is invalid. Check syntax error.";
        var problem = createProblemBuilder(status, INCOMPREHENSIBLE_MESSAGE, detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
    }

    @ExceptionHandler({ ValidationException.class })
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), BAD_REQUEST, request);
    }

    private ResponseEntity<Object> handlePropertyBindingException(PropertyBindingException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        var path = ex.getPath()
                .stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        var detail = String.format("The property '%s' does not exists. Correct or remove this property and try again.", path);

        var problem = createProblemBuilder(status, INCOMPREHENSIBLE_MESSAGE, detail)
                .userMessage(SYSTEM_ERROR_MSG)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
        var path = ex.getPath()
                .stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        var detail = String.format("Property '%s' has been given invalid value '%s'. Enter the value compatible with the type '%s'",
                path, ex.getValue(), ex.getTargetType().getSimpleName());

        var problem = createProblemBuilder(status, INCOMPREHENSIBLE_MESSAGE, detail)
                .userMessage(SYSTEM_ERROR_MSG)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        var status = NOT_FOUND;
        var detail = ex.getMessage();
        var problem = createProblemBuilder(status, RESOURCE_NOT_FOUND, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<Object> handleEntityInUseException(EntityInUseException ex, WebRequest request) {
        var status = CONFLICT;
        var detail = ex.getMessage();
        var problem = createProblemBuilder(status, ENTITY_IN_USE, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        var status = BAD_REQUEST;
        var detail = ex.getMessage();
        var problem = createProblemBuilder(status, ERROR_BUSINESS, detail)
                .userMessage(detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {

        var detail = String.format("The resource '%s' does not exist.", ex.getRequestURL());
        var problem = createProblemBuilder(status, RESOURCE_NOT_FOUND, detail)
                .userMessage(SYSTEM_ERROR_MSG)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        var status = INTERNAL_SERVER_ERROR;
        var detail = SYSTEM_ERROR_MSG;
        var problem = createProblemBuilder(status, SYSTEM_ERROR, detail)
                .userMessage(detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {

        if (body == null) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .userMessage(SYSTEM_ERROR_MSG)
                    .build();
        } else if (body instanceof String) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .userMessage(SYSTEM_ERROR_MSG)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemType problemType, String detail) {
        return Problem.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail);
    }

    private ResponseEntity<Object> handleValidationInternal(Exception ex, BindingResult bindingResult, HttpHeaders headers,
                                                            HttpStatus status, WebRequest request) {

        var detail = "One or more fields are invalid. Fill in correctly and try again.";

        List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());

                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }

                    return Problem.Object.builder()
                            .name(name)
                            .userMessage(message)
                            .build();
                })
                .collect(Collectors.toList());

        var problem = createProblemBuilder(status, INVALID_DATA, detail)
                .userMessage(detail)
                .objects(problemObjects)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }
}