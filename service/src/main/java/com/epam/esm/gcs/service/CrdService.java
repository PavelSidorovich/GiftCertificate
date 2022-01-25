package com.epam.esm.gcs.service;

import java.util.List;

public interface CrdService<T> {

    T create(T model);

    T findById(long id);

    List<T> findAll();

    void delete(long id);

}
