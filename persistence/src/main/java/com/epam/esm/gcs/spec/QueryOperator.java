package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueryOperator {

    EQUALS("="),
    LIKE("LIKE"),
    ORDER("ORDER");

    private final String operation;

}
