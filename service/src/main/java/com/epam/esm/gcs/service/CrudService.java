package com.epam.esm.gcs.service;

public interface CrudService<T> extends CrdService<T> {

    T update(T model);

}
