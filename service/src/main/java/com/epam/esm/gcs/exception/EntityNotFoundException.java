package com.epam.esm.gcs.exception;

public class EntityNotFoundException extends CommonEntityException {

    private static final String ERROR_MSG = "Requested resource (%s) not found (%s = '%s')";

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String entityField, Object fieldValue) {
        super(ERROR_MSG, entityName, entityField, fieldValue);
    }

}
