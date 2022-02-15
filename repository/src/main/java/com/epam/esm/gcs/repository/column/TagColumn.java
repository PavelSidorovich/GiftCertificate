package com.epam.esm.gcs.repository.column;

public enum TagColumn {

    ID("id"),
    NAME("name");

    private static final String MODEL_NAME = "gift_certificate_tag";

    private final String columnName;

    TagColumn(String columnName) {
        this.columnName = columnName;
    }

    public static String getModelName() {
        return MODEL_NAME;
    }

    public String getColumnName() {
        return columnName;
    }

}
