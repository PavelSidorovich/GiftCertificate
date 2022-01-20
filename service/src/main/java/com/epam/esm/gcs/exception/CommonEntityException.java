package com.epam.esm.gcs.exception;

public class CommonEntityException extends RuntimeException {

    public CommonEntityException(String message) {
        super(message);
    }

    public CommonEntityException(String errorMsg, String entityName, String entityField, Object fieldValue) {
        super(String.format(errorMsg, entityName, entityField, fieldValue));
    }

}
