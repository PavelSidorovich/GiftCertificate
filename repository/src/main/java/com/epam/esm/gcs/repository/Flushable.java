package com.epam.esm.gcs.repository;

public interface Flushable {

    void flush();

    void clear();

    void flushAndClear();

}
