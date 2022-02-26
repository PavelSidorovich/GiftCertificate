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
@Order(value = 2)
public class PurchaseExceptionHandler {

    private static final String NOT_ENOUGH_MONEY = "not.enough.money";

    private final MessageSource clientErrorMsgSource;

    @ExceptionHandler(NotEnoughMoneyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel handleNotEnoughMoney(NotEnoughMoneyException ex, Locale locale) {
        final String message = clientErrorMsgSource.getMessage(
                NOT_ENOUGH_MONEY, new Object[] {
                        ex.getCertificateName(),
                        ex.getCertificateCost(),
                        ex.getUserBalance()
                }, locale
        );
        log.error(message, ex);
        return new ResponseModel(HttpStatus.BAD_REQUEST, ex.getClazz(), message);
    }

}
