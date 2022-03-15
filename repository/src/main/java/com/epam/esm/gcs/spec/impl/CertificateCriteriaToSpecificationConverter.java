package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.model.CertificateModel_;
import com.epam.esm.gcs.model.TagModel_;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import com.epam.esm.gcs.spec.CriteriaToSpecificationConverter;
import com.epam.esm.gcs.spec.JoinColumnProps;
import com.epam.esm.gcs.spec.PageRequestFactory;
import com.epam.esm.gcs.spec.SearchFilter;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.spec.SearchQuery.SearchQueryBuilder;
import com.epam.esm.gcs.spec.SortOrder;
import com.epam.esm.gcs.spec.SortType;
import com.epam.esm.gcs.spec.SpecificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.epam.esm.gcs.spec.QueryOperator.*;

@Service
@AllArgsConstructor
public class CertificateCriteriaToSpecificationConverter
        implements CriteriaToSpecificationConverter<CertificateSearchCriteria> {

    private final SpecificationUtil specificationUtil;
    private final PageRequestFactory pageRequestFactory;

    @Override
    public <T> Pair<Specification<T>, Pageable> byCriteria(
            CertificateSearchCriteria searchCriteria, Class<T> clazz) {
        final List<SearchFilter> filters = getFilters(searchCriteria);
        final SortOrder sortOrder = getSortOrder(searchCriteria);
        final List<JoinColumnProps> joinColumnProps = getJoinColumnProps(searchCriteria);
        final SearchQueryBuilder searchQueryBuilder = SearchQuery
                .builder()
                .filters(filters)
                .sortOrder(sortOrder)
                .pageNumber(searchCriteria.getPage())
                .pageSize(searchCriteria.getPageSize());
        final SearchQuery searchQuery = joinColumnProps.isEmpty()
                ? searchQueryBuilder.build()
                : searchQueryBuilder.joinColumnProps(joinColumnProps).build();

        return Pair.of(specificationUtil.bySearchQuery(searchQuery, clazz),
                       pageRequestFactory.pageable(searchQuery));
    }

    private SortOrder getSortOrder(CertificateSearchCriteria searchCriteria) {
        final List<String> ascOrder = new ArrayList<>();
        final List<String> descOrder = new ArrayList<>();

        addSortOrder(ascOrder, descOrder, searchCriteria.getSortNameType(), CertificateModel_.name.getName());
        addSortOrder(ascOrder, descOrder, searchCriteria.getSortCreateDateType(),
                     CertificateModel_.createDate.getName());

        return new SortOrder(ascOrder, descOrder);
    }

    private List<SearchFilter> getFilters(CertificateSearchCriteria searchCriteria) {
        final List<SearchFilter> filters = new ArrayList<>();
        addFilter(filters, CertificateModel_.name.getName(), searchCriteria.getCertificateName());
        addFilter(filters, CertificateModel_.description.getName(), searchCriteria.getDescription());
        return filters;
    }

    private List<JoinColumnProps> getJoinColumnProps(CertificateSearchCriteria searchCriteria) {
        if (searchCriteria.getTagName() != null) {
            final SearchFilter joinTableFilter =
                    new SearchFilter(TagModel_.name.getName(), EQUALS, searchCriteria.getTagName());
            final JoinColumnProps joinColumnProp =
                    new JoinColumnProps(CertificateModel_.tags.getName(), joinTableFilter);
            return List.of(joinColumnProp);
        }
        return Collections.emptyList();
    }

    private void addFilter(List<SearchFilter> filters, String fieldName, String value) {
        if (value != null) {
            filters.add(new SearchFilter(fieldName, LIKE, value));
        }
    }

    private void addSortOrder(List<String> ascOrder, List<String> descOrder,
                              SortType sortNameType, String fieldName) {
        if (sortNameType == SortType.ASC) {
            ascOrder.add(fieldName);
        } else if (sortNameType == SortType.DESC) {
            descOrder.add(fieldName);
        }
    }

}
