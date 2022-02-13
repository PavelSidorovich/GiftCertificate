package com.epam.esm.gcs.service;

import java.util.List;

public interface Readable<T> {

    T findById(long id);

    List<T> findAll();

}
