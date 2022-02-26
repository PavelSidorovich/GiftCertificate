package com.epam.esm.gcs.util;

@FunctionalInterface
public interface FilterQueryGenerator<T> {

    String getSqlQuery(T filter);

}
