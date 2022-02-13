package com.epam.esm.gcs.repository.column;

public enum UserColumn {

    ID("id"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),
    EMAIL("email");

    private static final String MODEL_NAME = "user_account";

    private final String columnName;

    UserColumn(String columnName) {
        this.columnName = columnName;
    }

    public static String getModelName() {
        return MODEL_NAME;
    }

    public String getColumnName() {
        return columnName;
    }

}
