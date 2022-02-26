package com.epam.esm.gcs.service;

public interface Creatable<T> {

    T create(T model);

}
