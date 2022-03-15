package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.CertificateModel;
import com.epam.esm.gcs.model.CertificateModel_;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.CertificateRepository;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.spec.JoinColumnProps;
import com.epam.esm.gcs.spec.SearchFilter;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.spec.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
class PageRequestFactoryImplTest {

    private final PageRequestFactoryImpl pageRequestFactory;

    private final CertificateRepository certificateRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PageRequestFactoryImplTest(PageRequestFactoryImpl pageRequestFactory,
                                      CertificateRepository certificateRepository,
                                      TagRepository tagRepository) {
        this.pageRequestFactory = pageRequestFactory;
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
    }

    @BeforeEach
    void setUp() {
        TagModel tagModel1 = tagRepository.save(new TagModel("clothes"));
        TagModel tagModel2 = tagRepository.save(new TagModel("gift"));
        TagModel tagModel3 = tagRepository.save(new TagModel("device"));
        TagModel tagModel4 = tagRepository.save(new TagModel("app"));
        CertificateModel cert1 = CertificateModel
                .builder()
                .name("De facto discount")
                .description("10% discount")
                .price(BigDecimal.TEN)
                .tags(Set.of(tagModel1, tagModel2))
                .duration(10)
                .build();
        CertificateModel cert2 = CertificateModel
                .builder()
                .name("Repair device")
                .description("15% discount")
                .price(BigDecimal.ONE)
                .tags(Set.of(tagModel2, tagModel3, tagModel4))
                .duration(20)
                .build();
        CertificateModel cert3 = CertificateModel
                .builder()
                .name("LC buber")
                .description("free cloth")
                .price(BigDecimal.ZERO)
                .tags(Set.of(tagModel1))
                .duration(2)
                .build();
        CertificateModel cert4 = CertificateModel
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

    @ParameterizedTest
    @MethodSource("pagePropsProvider")
    void pageable_shouldReturnPageable_whenPageNumberAndPageSizeAreValid(
            Integer page, Integer pageSize, int expectedPage, int expectedPageSize) {
        final Pageable actual = pageRequestFactory.pageable(page, pageSize);

        assertEquals(expectedPage, actual.getPageNumber());
        assertEquals(expectedPageSize, actual.getPageSize());
    }

    private static Stream<Arguments> pagePropsProvider() {
        return Stream.of(
                Arguments.of(-1, null, 0, 20),
                Arguments.of(-1, 5, 0, 5),
                Arguments.of(10, 0, 10, 20),
                Arguments.of(null, 200, 0, 20)
        );
    }

    @ParameterizedTest
    @MethodSource("searchQueryProvider")
    void pageable_shouldReturnPageWithOrderedCertificates_whenOrderProvided(
            SearchQuery searchQuery, List<String> expectedNames) {
        final Pageable actualPage = pageRequestFactory.pageable(searchQuery);
        final List<CertificateModel> actualCertificates =
                certificateRepository.findAll(actualPage).getContent();

        assertEquals(0, actualPage.getPageNumber());
        assertEquals(10, actualPage.getPageSize());
        assertEquals(expectedNames.get(0), actualCertificates.get(0).getName());
        assertEquals(expectedNames.get(1), actualCertificates.get(1).getName());
        assertEquals(expectedNames.get(2), actualCertificates.get(2).getName());
        assertEquals(expectedNames.get(3), actualCertificates.get(3).getName());
    }

    private Stream<Arguments> searchQueryProvider() {
        return Stream.of(
                Arguments.of(getSearchQuery1(), List.of(
                        "smile media discount certificate",
                        "lc buber",
                        "repair device",
                        "de facto discount")
                ),
                Arguments.of(getSearchQuery2(), List.of(
                        "de facto discount",
                        "lc buber",
                        "repair device",
                        "smile media discount certificate")
                )
        );
    }

    private SearchQuery getSearchQuery1() {
        List<SearchFilter> filters = Collections.emptyList();
        List<JoinColumnProps> joinColumnProps = Collections.emptyList();
        SortOrder sortOrder = new SortOrder(
                Collections.emptyList(), List.of(CertificateModel_.createDate.getName())
        );
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

    private SearchQuery getSearchQuery2() {
        List<SearchFilter> filters = Collections.emptyList();
        List<JoinColumnProps> joinColumnProps = Collections.emptyList();
        SortOrder sortOrder = new SortOrder(
                List.of(CertificateModel_.name.getName()), Collections.emptyList()
        );
        return new SearchQuery(filters, joinColumnProps, sortOrder, 0, 10);
    }

}