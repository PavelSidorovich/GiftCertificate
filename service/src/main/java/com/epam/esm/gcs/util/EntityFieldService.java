package com.epam.esm.gcs.util;

import java.util.List;

public interface EntityFieldService {

    List<String> getNotNullFields(Object entity);

    List<String> getNotNullFields(Object entity, String... excludeNames);

}
