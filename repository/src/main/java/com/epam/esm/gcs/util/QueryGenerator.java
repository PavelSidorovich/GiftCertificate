package com.epam.esm.gcs.util;

import java.util.Map;

public interface QueryGenerator {

    QueryGenerator select(String jpaName);

    QueryGenerator selectDistinct(String jpaName);

    QueryGenerator delete();

    QueryGenerator where();

    QueryGenerator and(String column, String value, boolean like);

    QueryGenerator or(String column, String value, boolean like);

    QueryGenerator from(String jpaTable);

    QueryGenerator orderBy(Map<String, String> typeByColumn);

    QueryGenerator join(String jpaName, String asEntity);

    QueryGenerator leftJoin(String jpaName, String asEntity);

    String getSqlQuery();

    void reset();

}
