package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.exception.WiredEntityDeletionException;
import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
class GiftCertificateRepositoryImplTest {

    private final GiftCertificateRepositoryImpl certificateRepository;

    private final TagRepositoryImpl tagRepository;
    private final QueryLimiter queryLimiter;

    @Autowired
    public GiftCertificateRepositoryImplTest(
            GiftCertificateRepositoryImpl certificateRepository,
            TagRepositoryImpl tagRepository) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.queryLimiter = new QueryLimiter(10, 0);
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final GiftCertificateModel expected = getTestGiftCertificates().get(0);
        final GiftCertificateModel actual = certificateRepository.create(getTestGiftCertificates().get(0));

        assertTrue(actual.getId() > 0);
        assertEquals(expected.getName().toLowerCase(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(1, actual.getTags().size());
        assertEquals(actual.getCreateDate(), actual.getLastUpdateDate());
        assertEquals(1, actual.getTags().size());
    }

    @Test
    void create_shouldThrowDuplicateKeyException_ifNameIsNotUnique() {
        final GiftCertificateModel certificate = getTestGiftCertificates().get(0);
        final GiftCertificateModel copy = getTestGiftCertificates().get(0);

        certificateRepository.create(certificate);

        assertThrows(DataIntegrityViolationException.class, () ->
                certificateRepository.create(copy)
        );
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        final GiftCertificateModel expected =
                certificateRepository.create(getTestGiftCertificates().get(0));

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
        certificateRepository.create(getTestGiftCertificates().get(0));
        certificateRepository.create(getTestGiftCertificates().get(1));
        certificateRepository.create(getTestGiftCertificates().get(2));

        final List<GiftCertificateModel> actual1 =
                certificateRepository.findAll(new QueryLimiter(10, 0));
        final List<GiftCertificateModel> actual2 =
                certificateRepository.findAll(new QueryLimiter(1, 0));

        assertNotNull(actual1);
        assertEquals(3, actual1.size());
        assertNotNull(actual2);
        assertEquals(1, actual2.size());
    }

    @Test
    void findAll_shouldReturnLimitedList_whenHasLimits() {
        certificateRepository.create(getTestGiftCertificates().get(0));
        certificateRepository.create(getTestGiftCertificates().get(1));
        certificateRepository.create(getTestGiftCertificates().get(2));

        final List<GiftCertificateModel> actual =
                certificateRepository.findAll(new QueryLimiter(1, 0));

        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @Test
    void findByTags_shouldReturnCertificatesWithByTags_always() {
        GiftCertificateModel cert1 = certificateRepository.create(getTestGiftCertificates().get(0));
        GiftCertificateModel cert2 = getTestGiftCertificates().get(1);
        cert2.setTags(cert1.getTags());
        certificateRepository.create(cert2);
        certificateRepository.create(getTestGiftCertificates().get(2));

        final List<GiftCertificateModel> actual1 =
                certificateRepository.findByTags(Collections.emptyList(), queryLimiter);
        final List<GiftCertificateModel> actual2 =
                certificateRepository.findByTags(List.of("tagname1"), queryLimiter);
        final List<GiftCertificateModel> actual3 =
                certificateRepository.findByTags(List.of("tagname3", "tagname4"), queryLimiter);
        final List<GiftCertificateModel> actual4 =
                certificateRepository.findByTags(List.of("tagname4"), queryLimiter);

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
        certificateRepository.create(getTestGiftCertificates().get(0));
        certificateRepository.create(getTestGiftCertificates().get(1));
        certificateRepository.create(getTestGiftCertificates().get(2));

        GiftCertificateFilter filter1 = new GiftCertificateFilter("testName1", null, null, null, null);
        GiftCertificateFilter filter2 = new GiftCertificateFilter("testName1", "tagName3", null, null, null);
        GiftCertificateFilter filter3 = new GiftCertificateFilter(null, null, "desc", null, null);
        GiftCertificateFilter filter4 = new GiftCertificateFilter("testName", null, "desc", "DESC", null);

        final List<GiftCertificateModel> actual1 =
                certificateRepository.findByFilter(filter1, queryLimiter);
        final List<GiftCertificateModel> actual2 =
                certificateRepository.findByFilter(filter2, queryLimiter);
        final List<GiftCertificateModel> actual3 =
                certificateRepository.findByFilter(filter3, queryLimiter);
        final List<GiftCertificateModel> actual4 =
                certificateRepository.findByFilter(filter4, queryLimiter);

        assertEquals(1, actual1.size());
        assertEquals(0, actual2.size());
        assertEquals(3, actual3.size());
        assertEquals(3, actual4.size());
        assertEquals(1, actual4.get(0).getCreateDate().compareTo(actual4.get(1).getCreateDate()));
        assertEquals(1, actual4.get(1).getCreateDate().compareTo(actual4.get(2).getCreateDate()));
        assertEquals(1, actual4.get(0).getCreateDate().compareTo(actual4.get(2).getCreateDate()));
    }

    @Test
    void delete_shouldReturnTrue_whenIsDeleted() {
        final GiftCertificateModel certificate = certificateRepository.create(getTestGiftCertificates().get(0));
        final Long certificateId = certificate.getId();

        assertTrue(certificateRepository.delete(certificateId));
        assertTrue(certificateRepository.findById(certificateId).isEmpty());
    }

    @Test
    void delete_shouldReturnFalse_whenSuchRowNotExists() {
        assertFalse(certificateRepository.delete(100000L));
    }

    @Test
    void delete_shouldThrowWiredEntityDeletionException_whenEntityIsWired() {
        certificateRepository.create(getTestGiftCertificates().get(0));
        Optional<TagModel> tagOpt = tagRepository.findByName("tagName1");

        assertTrue(tagOpt.isPresent());
        assertThrows(WiredEntityDeletionException.class, () -> tagRepository.delete(tagOpt.get().getId()));
    }

    @Test
    void findByName_shouldReturnCertificateModel_ifExistsWithName() {
        final GiftCertificateModel expected =
                certificateRepository.create(getTestGiftCertificates().get(0));

        Optional<GiftCertificateModel> actualOptional =
                certificateRepository.findByName(expected.getName());

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
        Optional<GiftCertificateModel> actual = certificateRepository.findByName("testName");

        assertTrue(actual.isEmpty());
    }

    @Test
    void update_shouldUpdateCertificate_whenExistsWithId() {
        final GiftCertificateModel certificate =
                certificateRepository.create(getTestGiftCertificates().get(0));
        final Long id = certificate.getId();
        final GiftCertificateModel certificateToUpdate =
                GiftCertificateModel.builder()
                                    .id(id)
                                    .name("testName1")
                                    .description("newDescription")
                                    .price(BigDecimal.ONE)
                                    .duration(1)
                                    .tags(Set.of(new TagModel("lol")))
                                    .build();

        Optional<GiftCertificateModel> actualOptional = certificateRepository.update(certificateToUpdate);
        Optional<GiftCertificateModel> expectedOptional = certificateRepository.findById(id);

        assertFalse(actualOptional.isEmpty());
        assertFalse(expectedOptional.isEmpty());
        GiftCertificateModel actual = actualOptional.get();
        GiftCertificateModel expected = expectedOptional.get();
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertTrue(Objects.deepEquals(expected.getTags() != null? expected.getTags().toArray() : null,
                                      actual.getTags() != null? actual.getTags().toArray() : null));
    }

    @Test
    void update_shouldReturnOptionalEmpty_whenNotExistsWithId() {
        final GiftCertificateModel toUpdate =
                GiftCertificateModel.builder()
                                    .id(10000L)
                                    .name("testName")
                                    .description("newDescription")
                                    .price(BigDecimal.ONE)
                                    .duration(1)
                                    .build();

        assertTrue(certificateRepository.update(toUpdate).isEmpty());
    }

    @Test
    void existsWithName_shouldReturnTrue_ifSuchRowExists() {
        certificateRepository.create(getTestGiftCertificates().get(0));

        assertTrue(certificateRepository.existsWithName("testName1"));
    }

    @Test
    void existsWithName_shouldReturnFalse_ifSuchRowNotExists() {
        assertFalse(certificateRepository.existsWithName("testName"));
    }

    private List<GiftCertificateModel> getTestGiftCertificates() {
        GiftCertificateModel certificate1 =
                GiftCertificateModel.builder()
                                    .name("testName1")
                                    .description("testDescription1")
                                    .price(BigDecimal.TEN)
                                    .duration(10)
                                    .tags(Set.of(new TagModel("tagName1")))
                                    .build();
        GiftCertificateModel certificate2 =
                GiftCertificateModel.builder()
                                    .name("testName2")
                                    .description("testDescription2")
                                    .price(BigDecimal.ONE)
                                    .duration(30)
                                    .tags(Set.of(new TagModel("tagName2")))
                                    .build();
        GiftCertificateModel certificate3 =
                GiftCertificateModel.builder()
                                    .name("testName3")
                                    .description("testDescription3")
                                    .price(BigDecimal.TEN)
                                    .duration(5)
                                    .tags(Set.of(new TagModel("tagName3"), new TagModel("tagName4")))
                                    .build();
        return List.of(certificate1, certificate2, certificate3);
    }

}