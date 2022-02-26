package com.epam.esm.gcs.service;

import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface Readable<T> {

    T findById(long id);

    List<T> findAll(Limiter limiter);

}
