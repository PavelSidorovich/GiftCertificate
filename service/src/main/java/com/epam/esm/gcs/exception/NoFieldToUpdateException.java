package com.epam.esm.gcs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoFieldToUpdateException extends RuntimeException {

    private final Class<?> clazz;

}
