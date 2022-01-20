package com.epam.esm.gcs.exception;

public class DuplicatePropertyException extends CommonEntityException {

    private static final String ERROR_MSG = "Duplicate property for resource (%s) where (%s = '%s')";

    public DuplicatePropertyException(String message) {
        super(message);
    }

    public DuplicatePropertyException(String entityName, String entityField, Object fieldValue) {
        super(ERROR_MSG, entityName, entityField, fieldValue);
    }

}
