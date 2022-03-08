package com.epam.esm.gcs.spec;

import org.springframework.data.jpa.domain.Specification;

@FunctionalInterface
public interface SpecificationUtil {

    <T> Specification<T> bySearchQuery(SearchQuery searchQuery, Class<T> clazz);

}
