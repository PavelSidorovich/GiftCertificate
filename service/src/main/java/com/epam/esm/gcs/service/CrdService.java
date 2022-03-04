package com.epam.esm.gcs.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrdService<T> {

    T create(T model);

    List<T> findAll(Pageable pageable);

    T findById(long id);

    void delete(long id);

}
