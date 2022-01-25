package com.epam.esm.gcs.repository.mapper;

public enum TagColumn {

    ID("id"),
    NAME("name");

    private static final String MODEL_NAME = "tag";

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
