package com.epam.esm.gcs.repository;

import java.util.Optional;

public interface CrudRepository<T> extends CrdRepository<T> {

    Optional<T> update(T model);

}
