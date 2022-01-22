package com.epam.esm.gcs.exception;

public class DuplicatePropertyException extends CommonEntityException {

    public DuplicatePropertyException(Class<?> clazz, String entityField, Object fieldValue) {
        super(clazz, entityField, fieldValue);
    }

}
