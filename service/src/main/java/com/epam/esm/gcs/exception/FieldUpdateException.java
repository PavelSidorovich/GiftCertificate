package com.epam.esm.gcs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FieldUpdateException extends RuntimeException {

    private final Class<?> clazz;
    private final List<String> valuesByFields;

}
