package com.epam.esm.gcs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommonEntityException extends RuntimeException {

    private final Class<?> clazz;
    private final String entityField;
    private final Object fieldValue;

}
