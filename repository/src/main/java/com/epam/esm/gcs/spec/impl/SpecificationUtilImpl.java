package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.spec.JoinColumnProps;
import com.epam.esm.gcs.spec.SearchFilter;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.spec.SpecificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SpecificationUtilImpl implements SpecificationUtil {

    private static final String LIKE_VAL = "%";

    @Override
    public <T> Specification<T> bySearchQuery(SearchQuery searchQuery, Class<T> clazz) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            final List<JoinColumnProps> joinColumnProps = searchQuery.getJoinColumnProps();
            final List<SearchFilter> searchFilters = searchQuery.getFilters();

            if (joinColumnProps != null && !joinColumnProps.isEmpty()) {
                joinColumnProps.forEach(prop -> addJoinColumnProps(predicates, prop, criteriaBuilder, root));
            }
            if (searchFilters != null && !searchFilters.isEmpty()) {
                searchFilters.forEach(searchFilter -> addPredicates(predicates, searchFilter, criteriaBuilder, root));
            }
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private <T> void addJoinColumnProps(
            List<Predicate> predicates, JoinColumnProps joinColumnProp,
            CriteriaBuilder criteriaBuilder, Root<T> root) {
        final SearchFilter searchFilter = joinColumnProp.getSearchFilter();
        final Join<Object, Object> joinParent = root.join(joinColumnProp.getJoinColName());
        final String property = searchFilter.getField();
        final Path<Object> expression = joinParent.get(property);

        addPredicate(predicates, searchFilter, criteriaBuilder, expression);
    }

    private <T> void addPredicates(
            List<Predicate> predicates, SearchFilter searchFilter,
            CriteriaBuilder criteriaBuilder, Root<T> root) {
        final String property = searchFilter.getField();
        final Path<Object> expression = root.get(property);

        addPredicate(predicates, searchFilter, criteriaBuilder, expression);
    }

    private void addPredicate(List<Predicate> predicates, SearchFilter searchFilter,
                              CriteriaBuilder criteriaBuilder, Path expression) {
        switch (searchFilter.getOperator()) {
        case EQUALS:
            predicates.add(criteriaBuilder.equal(expression, searchFilter.getValue()));
            break;
        case LIKE:
            predicates.add(criteriaBuilder.like(expression, LIKE_VAL + searchFilter.getValue() + LIKE_VAL));
            break;
        default:
            log.error("{} is not a valid predicate", searchFilter.getOperator());
            throw new IllegalArgumentException();
        }
    }

}
