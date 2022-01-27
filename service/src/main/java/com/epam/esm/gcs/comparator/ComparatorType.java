package com.epam.esm.gcs.comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ComparatorType {

    CREATE_DATE_COMPARATOR("dateComparator"),
    NAME_COMPARATOR("nameComparator");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

}
