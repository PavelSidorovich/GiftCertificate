package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.GiftCertificateModel_;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.TagModel_;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.spec.JoinColumnProps;
import com.epam.esm.gcs.spec.QueryOperator;
import com.epam.esm.gcs.spec.SearchFilter;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.spec.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class SpecificationUtilImplTest {

    private final SpecificationUtilImpl specificationUtil;

    private final GiftCertificateRepository certificateRepository;
    private final TagRepository tagRepository;

    @Autowired
    public SpecificationUtilImplTest(SpecificationUtilImpl specificationUtil,
                                     GiftCertificateRepository certificateRepository,
                                     TagRepository tagRepository) {
        this.specificationUtil = specificationUtil;
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
    }

    @BeforeEach
    void setUp() {
        TagModel tagModel1 = tagRepository.save(new TagModel("clothes"));
        TagModel tagModel2 = tagRepository.save(new TagModel("gift"));
        TagModel tagModel3 = tagRepository.save(new TagModel("device"));
        TagModel tagModel4 = tagRepository.save(new TagModel("app"));
        GiftCertificateModel cert1 = GiftCertificateModel
                .builder()
                .name("De facto discount")
                .description("10% discount")
                .price(BigDecimal.TEN)
                .tags(Set.of(tagModel1, tagModel2))
                .duration(10)
                .build();
        GiftCertificateModel cert2 = GiftCertificateModel
                .builder()
                .name("Repair device")
                .description("15% discount")
                .price(BigDecimal.ONE)
                .tags(Set.of(tagModel2, tagModel3, tagModel4))
                .duration(20)
                .build();
        GiftCertificateModel cert3 = GiftCertificateModel
                .builder()
                .name("LC Vaikuki")
                .description("free cloth")
                .price(BigDecimal.ZERO)
                .tags(Set.of(tagModel1))
                .duration(2)
                .build();
        GiftCertificateModel cert4 = GiftCertificateModel
                .builder()
                .name("Smile media discount certificate")
                .description("7% discount")
                .price(BigDecimal.TEN)
                .tags(Set.of(tagModel2, tagModel4))
                .duration(30)
                .build();
        certificateRepository.save(cert1);
        certificateRepository.save(cert2);
        certificateRepository.save(cert3);
        certificateRepository.save(cert4);
    }

    @Test
    void bySearchQuery_shouldReturnCertificatesMatchingTheFilter1_always() {
        final SearchQuery searchQuery = getSearchQuery1();
        Specification<GiftCertificateModel> specification =
                specificationUtil.bySearchQuery(searchQuery, GiftCertificateModel.class);

        List<GiftCertificateModel> actual = certificateRepository.findAll(
                specification, PageRequest.of(0, 10)
        ).getContent();

        assertEquals(1, actual.size());
        assertEquals("repair device", actual.get(0).getName());
    }

    @Test
    void bySearchQuery_shouldReturnCertificatesMatchingTheFilter2_always() {
        final SearchQuery searchQuery = getSearchQuery2();
        Specification<GiftCertificateModel> specification =
                specificationUtil.bySearchQuery(searchQuery, GiftCertificateModel.class);

        List<GiftCertificateModel> actual = certificateRepository.findAll(
                specification, PageRequest.of(0, 10)
        ).getContent();

        assertEquals(3, actual.size());
        assertEquals("de facto discount", actual.get(0).getName());
        assertEquals("repair device", actual.get(1).getName());
        assertEquals("smile media discount certificate", actual.get(2).getName());
    }

    @Test
    void bySearchQuery_shouldReturnCertificatesMatchingTheFilter3_always() {
        final SearchQuery searchQuery = getSearchQuery3();
        Specification<GiftCertificateModel> specification =
                specificationUtil.bySearchQuery(searchQuery, GiftCertificateModel.class);

        List<GiftCertificateModel> actual = certificateRepository.findAll(
                specification, PageRequest.of(0, 10)
        ).getContent();

        assertEquals(2, actual.size());
        assertEquals("repair device", actual.get(0).getName());
        assertEquals("smile media discount certificate", actual.get(1).getName());
    }

    @Test
    void bySearchQuery_shouldReturnCertificatesMatchingTheFilter4_always() {
        final SearchQuery searchQuery = getSearchQuery4();
        Specification<GiftCertificateModel> specification =
                specificationUtil.bySearchQuery(searchQuery, GiftCertificateModel.class);

        List<GiftCertificateModel> actual = certificateRepository.findAll(
                specification, PageRequest.of(0, 10)
        ).getContent();

        assertEquals(1, actual.size());
        assertEquals("de facto discount", actual.get(0).getName());
    }

    @Test
    void bySearchQuery_shouldReturnCertificatesMatchingTheFilter5_always() {
        final SearchQuery searchQuery = getSearchQuery5();
        Specification<GiftCertificateModel> specification =
                specificationUtil.bySearchQuery(searchQuery, GiftCertificateModel.class);

        List<GiftCertificateModel> actual = certificateRepository.findAll(
                specification, PageRequest.of(0, 10)
        ).getContent();

        assertEquals(4, actual.size());
    }

    private SearchQuery getSearchQuery1() {
        SearchFilter filter1 = new SearchFilter(
                GiftCertificateModel_.name.getName(), QueryOperator.EQUALS, "repair device");
        SearchFilter filter2 = new SearchFilter(
                GiftCertificateModel_.description.getName(), QueryOperator.LIKE, "15");
        SearchFilter filter3 = new SearchFilter(
                TagModel_.name.getName(), QueryOperator.LIKE, "dev");
        List<SearchFilter> filters = List.of(filter1, filter2);
        List<JoinColumnProps> joinColumnProps = List.of(
                new JoinColumnProps(GiftCertificateModel_.tags.getName(), filter3));
        SortOrder sortOrder = new SortOrder(List.of(
                GiftCertificateModel_.createDate.getName()), List.of(GiftCertificateModel_.name.getName())
        );
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

    private SearchQuery getSearchQuery2() {
        SearchFilter filter1 = new SearchFilter(
                GiftCertificateModel_.description.getName(), QueryOperator.LIKE, "discount");
        List<SearchFilter> filters = List.of(filter1);
        List<JoinColumnProps> joinColumnProps = Collections.emptyList();
        SortOrder sortOrder = new SortOrder(Collections.emptyList(), Collections.emptyList());
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

    private SearchQuery getSearchQuery3() {
        SearchFilter filter1 = new SearchFilter(
                GiftCertificateModel_.description.getName(), QueryOperator.LIKE, "discount");
        SearchFilter filter2 = new SearchFilter(
                TagModel_.name.getName(), QueryOperator.LIKE, "app");
        List<SearchFilter> filters = List.of(filter1);
        List<JoinColumnProps> joinColumnProps = List.of(
                new JoinColumnProps(GiftCertificateModel_.tags.getName(), filter2));
        SortOrder sortOrder = new SortOrder(List.of(
                GiftCertificateModel_.createDate.getName()), List.of(GiftCertificateModel_.name.getName())
        );
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

    private SearchQuery getSearchQuery4() {
        SearchFilter filter1 = new SearchFilter(
                GiftCertificateModel_.description.getName(), QueryOperator.LIKE, "discount");
        SearchFilter filter2 = new SearchFilter(
                TagModel_.name.getName(), QueryOperator.LIKE, "clothes");
        List<SearchFilter> filters = List.of(filter1);
        List<JoinColumnProps> joinColumnProps = List.of(
                new JoinColumnProps(GiftCertificateModel_.tags.getName(), filter2));
        SortOrder sortOrder = new SortOrder(Collections.emptyList(), Collections.emptyList());
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

    private SearchQuery getSearchQuery5() {
        List<SearchFilter> filters = Collections.emptyList();
        List<JoinColumnProps> joinColumnProps = Collections.emptyList();
        SortOrder sortOrder = new SortOrder(Collections.emptyList(), Collections.emptyList());
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

}