package com.epam.esm.gcs.repository;

public interface CRUDRepository<T> extends CRDRepository<T> {

    T update(T model);

}
