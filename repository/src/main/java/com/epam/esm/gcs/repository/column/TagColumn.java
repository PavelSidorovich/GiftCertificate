package com.epam.esm.gcs.repository.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagColumn {

    ID("id"),
    NAME("name");

    private static final String MODEL_NAME = "gift_certificate_tag";

    private final String columnName;

    public static String getModelName() {
        return MODEL_NAME;
    }

}
