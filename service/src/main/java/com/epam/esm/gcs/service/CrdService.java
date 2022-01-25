package com.epam.esm.gcs.service;

import java.util.List;

public interface CRDService<T> {

    T create(T model);

    T findById(long id);

    List<T> findAll();

    void delete(long id);

}
