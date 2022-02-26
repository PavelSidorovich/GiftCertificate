package com.epam.esm.gcs.exception;

public class NoFieldToUpdateException extends CommonEntityException {

    public NoFieldToUpdateException(Class<?> clazz) {
        super(clazz);
    }

}
