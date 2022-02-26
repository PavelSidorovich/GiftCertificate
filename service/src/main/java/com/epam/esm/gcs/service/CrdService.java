package com.epam.esm.gcs.service;

import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface CrdService<T> {

    T create(T model);

    List<T> findAll(Limiter limiter);

    T findById(long id);

    void delete(long id);

}
