package com.epam.esm.gcs.filter;

public enum SortType {
    ASC,
    DESC,
    NONE;

    public static SortType getSortType(String sortType) {
        try {
            return valueOf(sortType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }

}
