package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class GiftCertificateRepositoryImplTest {

    private final GiftCertificateRepositoryImpl certificateRepository;

    @Autowired
    public GiftCertificateRepositoryImplTest(
            GiftCertificateRepositoryImpl certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final GiftCertificateModel expected = getTestGiftCertificates().get(0);
        final GiftCertificateModel actual = certificateRepository.create(getTestGiftCertificates().get(0));

        expected.setId(actual.getId());
        expected.setCreateDate(actual.getCreateDate());
        expected.setLastUpdateDate(actual.getLastUpdateDate());

        assertEquals(expected, actual);
        assertTrue(actual.getId().compareTo(0L) > 0);
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

        final List<GiftCertificateModel> actual =
                certificateRepository.findAll(new QueryLimiter(10, 0));

        assertNotNull(actual);
        assertEquals(3, actual.size());
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
        final GiftCertificateModel certificateToUpdate =
                GiftCertificateModel.builder()
                                    .id(certificate.getId())
                                    .name("testName1")
                                    .description("newDescription")
                                    .price(BigDecimal.ONE)
                                    .duration(1)
                                    .tags(List.of(new TagModel("lol")))
                                    .build();

        Optional<GiftCertificateModel> actualOptional = certificateRepository.update(certificateToUpdate);
        Optional<GiftCertificateModel> expectedOptional = certificateRepository.findByName("testName1");

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
                                    .tags(new ArrayList<>())
                                    .build();
        GiftCertificateModel certificate2 =
                GiftCertificateModel.builder()
                                    .name("testName2")
                                    .description("testDescription2")
                                    .price(BigDecimal.ONE)
                                    .duration(30)
                                    .tags(new ArrayList<>())
                                    .build();
        GiftCertificateModel certificate3 =
                GiftCertificateModel.builder()
                                    .name("testName3")
                                    .description("testDescription3")
                                    .price(BigDecimal.TEN)
                                    .duration(5)
                                    .tags(new ArrayList<>())
                                    .build();
        return List.of(certificate1, certificate2, certificate3);
    }

}