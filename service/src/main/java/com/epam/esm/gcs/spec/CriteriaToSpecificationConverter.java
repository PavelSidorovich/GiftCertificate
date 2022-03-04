package com.epam.esm.gcs.spec;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@FunctionalInterface
public interface CriteriaToSpecificationConverter<T> {

    <C> Pair<Specification<C>, Pageable> byCriteria(T searchCriteria, Class<C> clazz);

}
