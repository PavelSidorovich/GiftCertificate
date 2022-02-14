package com.epam.esm.gcs.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class NotEnoughMoneyException extends CommonEntityException {

    private final String certificateName;
    private final BigDecimal certificateCost;
    private final BigDecimal userBalance;

    public NotEnoughMoneyException(Class<?> clazz, String certificateName,
                                   BigDecimal certificateCost, BigDecimal userBalance) {
        super(clazz);
        this.certificateName = certificateName;
        this.certificateCost = certificateCost;
        this.userBalance = userBalance;
    }

}
