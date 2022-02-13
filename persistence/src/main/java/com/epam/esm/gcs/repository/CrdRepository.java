package com.epam.esm.gcs.repository;

import java.util.List;
import java.util.Optional;

public interface CrdRepository<T> extends Flushable {

    T create(T model);

    Optional<T> findById(long id);

    List<T> findAll();

    boolean delete(long id);

}
