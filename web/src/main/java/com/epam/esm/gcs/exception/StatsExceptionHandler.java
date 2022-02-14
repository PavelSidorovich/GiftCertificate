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
@Order(value = 3)
public class StatsExceptionHandler {

    private static final String NO_WIDELY_USED_TAG = "no.widely.used.tag";

    private final MessageSource clientErrorMsgSource;

    @ExceptionHandler(NoWidelyUsedTagException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseModel handleNoWidelyUsedTag(NoWidelyUsedTagException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                NO_WIDELY_USED_TAG, null, locale
        );
        log.error(message, ex);

        return new ResponseModel(HttpStatus.NOT_FOUND, ex.getClazz(), message);
    }

}
