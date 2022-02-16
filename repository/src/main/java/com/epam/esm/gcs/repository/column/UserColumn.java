package com.epam.esm.gcs.repository.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserColumn {

    ID("id"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),
    EMAIL("email");

    private static final String MODEL_NAME = "user_account";

    private final String columnName;

    public static String getModelName() {
        return MODEL_NAME;
    }

}
