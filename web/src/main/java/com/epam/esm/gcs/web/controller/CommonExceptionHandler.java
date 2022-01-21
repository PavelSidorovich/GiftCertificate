package com.epam.esm.gcs.web.controller;

import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.web.response.ResponseModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@AllArgsConstructor(onConstructor_ = { @Autowired })
public class CommonExceptionHandler {

    private static final String RESOURCE_NOT_FOUND = "resource.not.found";
    private static final String DUPLICATE_PROPERTY = "duplicate.property";
    private static final String ERROR_NOT_DEFINED = "error.not.defined";
    private static final String METHOD_NOT_ALLOWED = "method.not.allowed";
    private static final String TYPE_MISMATCH = "type.mismatch";
    private static final String MESSAGE_NOT_READABLE = "message.not.readable";

    private final MessageSource clientErrorMsgSource;
    private final MessageSource serverErrorMsgSource;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseModel handleEntityNotFound(EntityNotFoundException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                RESOURCE_NOT_FOUND, new Object[] {
                        ex.getEntityField(), ex.getFieldValue()
                }, locale
        );
        return new ResponseModel(HttpStatus.NOT_FOUND, ex.getClazz(), message);
    }

    @ExceptionHandler(DuplicatePropertyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseModel handleDuplicateProperty(DuplicatePropertyException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                DUPLICATE_PROPERTY, new Object[] {
                        ex.getEntityField(), ex.getFieldValue()
                }, locale
        );
        return new ResponseModel(HttpStatus.CONFLICT, ex.getClazz(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel handleArgumentNotValid(MethodArgumentNotValidException ex,
                                                Locale locale) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getAllErrors().stream()
                                      .map(objectError -> clientErrorMsgSource.getMessage(objectError, locale))
                                      .collect(Collectors.joining("; ", "", "."));

        return new ResponseModel(HttpStatus.BAD_REQUEST, Objects.requireNonNull(
                bindingResult.getTarget()).getClass(), message
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseModel handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                  Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                METHOD_NOT_ALLOWED, new Object[] { ex.getMethod() }, locale
        );
        return new ResponseModel(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseModel handleTypeMismatch(MethodArgumentTypeMismatchException ex, Locale locale) {
        final Class<?> requiredType = Objects.requireNonNull(ex.getRequiredType());
        final Object providedValue = ex.getValue();
        final String message = clientErrorMsgSource.getMessage(
                TYPE_MISMATCH, new Object[] { providedValue, requiredType }, locale
        );
        return new ResponseModel(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel handleNotReadableMessage(HttpMessageNotReadableException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                MESSAGE_NOT_READABLE, null, locale
        );
        return new ResponseModel(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel handleCommonErrors(Exception ex, Locale locale) {
        final String message = serverErrorMsgSource.getMessage(
                ERROR_NOT_DEFINED, null, locale
        );
        return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
