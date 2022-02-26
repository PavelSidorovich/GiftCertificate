package com.epam.esm.gcs.exception;

public class EntityNotFoundException extends EntityFieldException {

    public EntityNotFoundException(Class<?> clazz, String entityField, Object fieldValue) {
        super(clazz, entityField, fieldValue);
    }

}
