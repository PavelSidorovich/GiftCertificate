package com.epam.esm.gcs.exception;

import lombok.Getter;

@Getter
public class EntityFieldException extends CommonEntityException {

    private final String entityField;
    private final Object fieldValue;

    public EntityFieldException(Class<?> clazz, String entityField, Object fieldValue) {
        super(clazz);
        this.entityField = entityField;
        this.fieldValue = fieldValue;
    }

}
