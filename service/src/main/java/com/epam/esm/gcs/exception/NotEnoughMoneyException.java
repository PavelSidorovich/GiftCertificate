package com.epam.esm.gcs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class NotEnoughMoneyException extends RuntimeException {

    private final String certificateName;
    private final BigDecimal certificateCost;
    private final BigDecimal userBalance;
    private final Class<?> clazz;

}
