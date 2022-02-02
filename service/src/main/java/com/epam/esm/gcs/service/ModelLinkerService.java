package com.epam.esm.gcs.service;

import java.util.List;

public interface ModelLinkerService<T> {

    void link(T model);

    void unlink(T model);

    T findById(long id);

    List<T> findAll();

}
