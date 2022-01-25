package com.epam.esm.gcs.service;

public interface CRUDService<T> extends CRDService<T> {

    T update(T model);

}
