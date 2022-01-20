package com.epam.esm.gcs.service;

import java.util.List;
import java.util.Optional;

public interface CRDService<T> {

    T create(T model);

    T findById(long id);

    List<T> findAll();

    void delete(long id);

}
