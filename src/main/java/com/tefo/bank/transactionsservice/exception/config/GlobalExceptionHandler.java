package com.tefo.bank.transactionsservice.exception.config;

import com.tefo.library.commonutils.constants.ValidationMessages;
import com.tefo.library.commonutils.exception.EntityNotFoundException;
import com.tefo.library.commonutils.exception.UniqueConstraintViolationException;
import com.tefo.library.commonutils.exception.utils.ExceptionDetails;
import com.tefo.library.commonutils.exception.utils.FieldErrorDetails;
import com.tefo.library.customdata.field.exception.ValidationRuleException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;


/**
 * Global exception handler for the transaction-service.
 * Handles exceptions that occur within the application and provides an appropriate response to the client.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationRuleException.class)
    public ResponseEntity<ExceptionDetails> handleValidationRulesExceptions(ValidationRuleException ex) {
        return new ResponseEntity<>(getExceptionDetailsObject(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDetails> handleGeneralExceptions(Exception ex) {
        return new ResponseEntity<>(getExceptionDetailsObject(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(getExceptionDetailsObject(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handleNotFoundExceptions(EntityNotFoundException ex) {
        return new ResponseEntity<>(getExceptionDetailsObject(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDetails> handleValidationExceptions(ConstraintViolationException ex) {
        List<FieldErrorDetails> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(field ->
                        FieldErrorDetails.builder()
                                .fieldName(field.getPropertyPath().toString())
                                .errorMessage(field.getMessage())
                                .build()
                )
                .toList();
        return createValidationExceptionsResponse(fieldErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDetails> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError ->
                        FieldErrorDetails.builder()
                                .fieldName(fieldError.getField())
                                .errorMessage(fieldError.getDefaultMessage())
                                .build()
                )
                .toList();
        return createValidationExceptionsResponse(fieldErrors);
    }

    @ExceptionHandler(UniqueConstraintViolationException.class)
    public ResponseEntity<ExceptionDetails> handleUniqueValidationException(UniqueConstraintViolationException ex) {
        return createValidationExceptionsResponse(List.of(
                FieldErrorDetails.builder()
                        .fieldName(ex.getFieldName())
                        .errorMessage(ex.getMessage())
                        .build()
        ));
    }

    private ExceptionDetails getExceptionDetailsObject(List<FieldErrorDetails> fieldErrors, String message) {
        return ExceptionDetails.builder()
                .fieldErrorDetails(fieldErrors)
                .message(message)
                .build();
    }

    private ExceptionDetails getExceptionDetailsObject(String message) {
        return ExceptionDetails.builder()
                .message(message)
                .build();
    }

    private ResponseEntity<ExceptionDetails> createValidationExceptionsResponse(List<FieldErrorDetails> fieldErrors) {
        return new ResponseEntity<>(getExceptionDetailsObject(
                fieldErrors, ValidationMessages.VALIDATION_VIOLATION_MESSAGE), HttpStatus.BAD_REQUEST);
    }
}
