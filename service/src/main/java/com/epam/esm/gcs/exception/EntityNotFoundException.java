package com.epam.esm.gcs.exception;

public class EntityNotFoundException extends CommonEntityException {

    public EntityNotFoundException(Class<?> clazz, String entityField, String fieldValue) {
        super(clazz, entityField, fieldValue);
    }

}
