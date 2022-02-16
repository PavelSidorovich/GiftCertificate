package com.epam.esm.gcs.exception;

public class WiredEntityDeletionException extends EntityFieldException {

    public WiredEntityDeletionException(Class<?> clazz, String entityField, Object fieldValue) {
        super(clazz, entityField, fieldValue);
    }

}
