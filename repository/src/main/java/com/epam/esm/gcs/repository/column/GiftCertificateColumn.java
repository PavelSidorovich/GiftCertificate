package com.epam.esm.gcs.repository.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum GiftCertificateColumn {

    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration"),
    CREATE_DATE("create_date"),
    LAST_UPDATE_DATE("last_update_date");

    private static final String MODEL_NAME = "gift_certificate";

    private final String columnName;

    public static String getModelName() {
        return MODEL_NAME;
    }

    public static String[] getColumnNames() {
        return EnumSet.range(NAME, LAST_UPDATE_DATE).stream()
                      .map(GiftCertificateColumn::getColumnName)
                      .toArray(String[]::new);
    }

}
