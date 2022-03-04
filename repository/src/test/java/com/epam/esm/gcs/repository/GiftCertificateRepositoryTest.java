package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.GiftCertificateModel_;
import com.epam.esm.gcs.model.TagModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class GiftCertificateRepositoryTest {

    private final GiftCertificateRepository certificateRepository;

    private final TagRepository tagRepository;
    private final PageRequest pageRequest;

    private Set<TagModel> tags1;
    private Set<TagModel> tags2;
    private Set<TagModel> tags3;

    @Autowired
    public GiftCertificateRepositoryTest(
            GiftCertificateRepository certificateRepository,
            TagRepository tagRepository) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.pageRequest = PageRequest.of(0, 10);
    }

    @BeforeEach
    void setUp() {
        TagModel tag1 = tagRepository.save(new TagModel("tagName1"));
        TagModel tag2 = tagRepository.save(new TagModel("tagName2"));
        TagModel tag3 = tagRepository.save(new TagModel("tagName3"));
        TagModel tag4 = tagRepository.save(new TagModel("tagName4"));
        tags1 = Set.of(tag1, tag2);
        tags2 = Set.of(tag1, tag3);
        tags3 = Set.of(tag2, tag3, tag4);
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final GiftCertificateModel expected = getTestGiftCertificates(tags1).get(0);
        final GiftCertificateModel actual = certificateRepository.save(getTestGiftCertificates(tags1).get(0));

        assertTrue(actual.getId() > 0);
        assertEquals(expected.getName().toLowerCase(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(2, actual.getTags().size());
        assertEquals(actual.getCreateDate(), actual.getLastUpdateDate());
    }

    @Test
    void create_shouldThrowDuplicateKeyException_ifNameIsNotUnique() {
        final GiftCertificateModel certificate = getTestGiftCertificates(tags1).get(0);
        final GiftCertificateModel copy = getTestGiftCertificates(tags1).get(0);

        certificateRepository.save(certificate);

        assertThrows(DataIntegrityViolationException.class, () ->
                certificateRepository.save(copy)
        );
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        final GiftCertificateModel expected =
                certificateRepository.save(getTestGiftCertificates(tags1).get(0));

        Optional<GiftCertificateModel> actualCertificate =
                certificateRepository.findById(expected.getId());

        assertTrue(actualCertificate.isPresent());
        GiftCertificateModel actual = actualCertificate.get();
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(expected.getDuration(), actual.getDuration());
        assertTrue(Objects.deepEquals(expected.getTags() != null? expected.getTags().toArray() : null,
                                      actual.getTags() != null? actual.getTags().toArray() : null));
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifNotExistsWithId() {
        Optional<GiftCertificateModel> actualCertificate = certificateRepository.findById(100000L);

        assertTrue(actualCertificate.isEmpty());
    }

    @Test
    void findAll_shouldReturnListOfTags_always() {
        certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        certificateRepository.save(getTestGiftCertificates(tags1).get(1));
        certificateRepository.save(getTestGiftCertificates(tags1).get(2));

        final List<GiftCertificateModel> actual1 =
                certificateRepository.findAll(PageRequest.of(0, 10)).getContent();
        final List<GiftCertificateModel> actual2 =
                certificateRepository.findAll(PageRequest.of(0, 1)).getContent();

        assertNotNull(actual1);
        assertEquals(3, actual1.size());
        assertNotNull(actual2);
        assertEquals(1, actual2.size());
    }

    @Test
    void findAll_shouldReturnLimitedList_whenHasLimits() {
        certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        certificateRepository.save(getTestGiftCertificates(tags1).get(1));
        certificateRepository.save(getTestGiftCertificates(tags1).get(2));

        final List<GiftCertificateModel> actual =
                certificateRepository.findAll(PageRequest.of(0, 1)).getContent();

        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @Test
    void findByTags_shouldReturnCertificatesWithByTags_always() {
        certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        certificateRepository.save(getTestGiftCertificates(tags2).get(1));
        certificateRepository.save(getTestGiftCertificates(tags3).get(2));

        final List<GiftCertificateModel> actual1 =
                certificateRepository.findByTags(Collections.emptyList(), 0L, pageRequest);
        final List<GiftCertificateModel> actual2 =
                certificateRepository.findByTags(List.of("tagname1"), 1L, pageRequest);
        final List<GiftCertificateModel> actual3 =
                certificateRepository.findByTags(List.of("tagname3", "tagname4"), 2L, pageRequest);
        final List<GiftCertificateModel> actual4 =
                certificateRepository.findByTags(List.of("tagname4"), 1L, pageRequest);

        assertNotNull(actual1);
        assertEquals(0, actual1.size());
        assertNotNull(actual2);
        assertEquals(2, actual2.size());
        assertNotNull(actual3);
        assertEquals(1, actual3.size());
        assertNotNull(actual4);
        assertEquals(1, actual4.size());
    }

    @Test
    void findByFilter_shouldReturnCertificatesByAppliedFilter_always() {
        certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        certificateRepository.save(getTestGiftCertificates(tags2).get(1));
        certificateRepository.save(getTestGiftCertificates(tags3).get(2));
        Specification<GiftCertificateModel> specification1 = (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get(GiftCertificateModel_.price.getName()), BigDecimal.TEN);
        Specification<GiftCertificateModel> specification2 = specification1.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                        root.get(GiftCertificateModel_.price.getName()), BigDecimal.TEN));

        final List<GiftCertificateModel> actual =
                certificateRepository.findAll(specification2, pageRequest).getContent();

        assertEquals(2, actual.size());
        assertEquals(0, actual.get(0).getPrice().compareTo(BigDecimal.TEN));
        assertEquals(0, actual.get(1).getPrice().compareTo(BigDecimal.TEN));
    }

    @Test
    void delete_shouldDeleteCertificate_whenFound() {
        final GiftCertificateModel certificate =
                certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        final Long certificateId = certificate.getId();

        certificateRepository.deleteById(certificateId);

        assertTrue(certificateRepository.findById(certificateId).isEmpty());
    }

    @Test
    void delete_shouldNotDeleteTag_whenEntityIsWired() {
        certificateRepository.save(getTestGiftCertificates(tags1).get(0));
        Optional<TagModel> tagOpt = tagRepository.findByNameIgnoreCase("tagName1");

        assertTrue(tagOpt.isPresent());

        tagRepository.deleteById(tagOpt.get().getId());

        assertThrows(DataIntegrityViolationException.class, tagRepository::findAll);
    }

    @Test
    void delete_shouldNotDeleteTag_whenEntityNotExists() {
        assertThrows(EmptyResultDataAccessException.class, () -> tagRepository.deleteById(10000L));
    }

    @Test
    void findByName_shouldReturnCertificateModel_ifExistsWithName() {
        final GiftCertificateModel expected =
                certificateRepository.save(getTestGiftCertificates(tags1).get(0));

        Optional<GiftCertificateModel> actualOptional =
                certificateRepository.findByNameIgnoreCase(expected.getName());

        assertTrue(actualOptional.isPresent());
        GiftCertificateModel actual = actualOptional.get();
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(expected.getDuration(), actual.getDuration());
        assertTrue(Objects.deepEquals(expected.getTags() != null? expected.getTags().toArray() : null,
                                      actual.getTags() != null? actual.getTags().toArray() : null));
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotExistsWithName() {
        Optional<GiftCertificateModel> actual = certificateRepository.findByNameIgnoreCase("testName");

        assertTrue(actual.isEmpty());
    }

    @Test
    void existsWithName_shouldReturnFalse_ifSuchRowNotExists() {
        assertFalse(certificateRepository.existsByNameIgnoreCase("testName"));
    }

    private List<GiftCertificateModel> getTestGiftCertificates(Set<TagModel> tags) {
        GiftCertificateModel certificate1 =
                GiftCertificateModel.builder()
                                    .name("testName1")
                                    .description("testDescription1")
                                    .price(BigDecimal.TEN)
                                    .duration(10)
                                    .tags(tags)
                                    .build();
        GiftCertificateModel certificate2 =
                GiftCertificateModel.builder()
                                    .name("testName2")
                                    .description("testDescription2")
                                    .price(BigDecimal.ONE)
                                    .duration(30)
                                    .tags(tags)
                                    .build();
        GiftCertificateModel certificate3 =
                GiftCertificateModel.builder()
                                    .name("testName3")
                                    .description("testDescription3")
                                    .price(BigDecimal.TEN)
                                    .duration(5)
                                    .tags(tags)
                                    .build();
        return List.of(certificate1, certificate2, certificate3);
    }
}
