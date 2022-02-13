package com.epam.esm.gcs.service;

public interface Updatable<T> {

    T update(T model);

}
