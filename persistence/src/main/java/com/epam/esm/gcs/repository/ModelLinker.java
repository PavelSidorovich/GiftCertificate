package com.epam.esm.gcs.repository;

import java.util.List;
import java.util.Optional;

public interface ModelLinker<T> {

    void link(T model);

    void unlink(T model);

    Optional<T> findById(long id);

    List<T> findAll();

}
