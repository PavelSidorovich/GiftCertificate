package com.epam.esm.gcs.exception;

public class NoFieldsToUpdateException extends CommonEntityException {

    public NoFieldsToUpdateException(Class<?> clazz) {
        super(clazz);
    }

}
