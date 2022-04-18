package com.epam.esm.gcs.util;

import java.util.List;

public interface EntityMapper {

    <T, F> F map(T model, Class<F> clazz);

    <T, F> List<F> map(List<T> list, Class<F> clazz);

}
