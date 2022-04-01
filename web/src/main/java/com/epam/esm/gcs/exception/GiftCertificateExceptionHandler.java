package com.epam.esm.gcs.exception;

import com.epam.esm.gcs.response.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
@Order(value = 1)
public class GiftCertificateExceptionHandler {

    private static final String SEVERAL_FIELD_UPDATE = "model.fields.update";
    private static final String NO_FIELD_TO_UPDATE = "model.field.empty.update";

    private final MessageSource clientErrorMsgSource;

    @ExceptionHandler(FieldUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel handleSeveralFieldsToUpdateError(FieldUpdateException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                SEVERAL_FIELD_UPDATE, new Object[] {
                        String.join(", ", ex.getValuesByFields())
                }, locale
        );
        log.error(message, ex);
        return new ResponseModel(HttpStatus.BAD_REQUEST, ex.getClazz(), message);
    }

    @ExceptionHandler(NoFieldsToUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel handleNoFieldToUpdate(NoFieldsToUpdateException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                NO_FIELD_TO_UPDATE, null, locale
        );
        log.error(message, ex);
        return new ResponseModel(HttpStatus.BAD_REQUEST, ex.getClazz(), message);
    }

}
