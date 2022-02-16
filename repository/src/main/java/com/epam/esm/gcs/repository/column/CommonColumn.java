package com.epam.esm.gcs.repository.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonColumn {

    ID("id");

    private final String columnName;

}
