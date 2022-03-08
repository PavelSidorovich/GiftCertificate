package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.CertificateModel;
import com.epam.esm.gcs.model.CertificateModel_;
import com.epam.esm.gcs.model.TagModel_;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import com.epam.esm.gcs.spec.JoinColumnProps;
import com.epam.esm.gcs.spec.PageRequestFactory;
import com.epam.esm.gcs.spec.QueryOperator;
import com.epam.esm.gcs.spec.SearchFilter;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.spec.SortOrder;
import com.epam.esm.gcs.spec.SpecificationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.Mockito.*;

@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
@ExtendWith(MockitoExtension.class)
class CertificateCriteriaToSpecificationConverterTest {

    private final CertificateCriteriaToSpecificationConverter converter;

    private final SpecificationUtil specificationUtil;
    private final PageRequestFactory pageRequestFactory;

    public CertificateCriteriaToSpecificationConverterTest(
            @Mock SpecificationUtil specificationUtil,
            @Mock PageRequestFactory pageRequestFactory) {
        this.specificationUtil = specificationUtil;
        this.pageRequestFactory = pageRequestFactory;
        this.converter = new CertificateCriteriaToSpecificationConverter(
                specificationUtil, pageRequestFactory
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void byCriteria_shouldReturnSpecificationForSearchQuery_always() {
        final CertificateSearchCriteria searchCriteria = new CertificateSearchCriteria(
                PageRequest.of(0, 10), "certName", "tagName",
                "desc", "ASC", "DESC"
        );
        final SearchQuery searchQuery = getSearchQuery();
        final Specification<CertificateModel> specification =
                (Specification<CertificateModel>) mock(Specification.class);
        when(specificationUtil.bySearchQuery(searchQuery, CertificateModel.class))
                .thenReturn(specification);
        when(pageRequestFactory.pageable(searchQuery))
                .thenReturn(PageRequest.of(0, 10));

        converter.byCriteria(searchCriteria, CertificateModel.class);

        verify(specificationUtil).bySearchQuery(searchQuery, CertificateModel.class);
        verify(pageRequestFactory).pageable(searchQuery);
    }

    private SearchQuery getSearchQuery() {
        SearchFilter filter1 = new SearchFilter(
                CertificateModel_.name.getName(), QueryOperator.LIKE, "certName");
        SearchFilter filter2 = new SearchFilter(
                CertificateModel_.description.getName(), QueryOperator.LIKE, "desc");
        SearchFilter filter3 = new SearchFilter(
                TagModel_.name.getName(), QueryOperator.EQUALS, "tagName");
        List<SearchFilter> filters = List.of(filter1, filter2);
        List<JoinColumnProps> joinColumnProps = List.of(
                new JoinColumnProps(CertificateModel_.tags.getName(), filter3));
        SortOrder sortOrder = new SortOrder(List.of(
                CertificateModel_.createDate.getName()), List.of(CertificateModel_.name.getName())
        );
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

}