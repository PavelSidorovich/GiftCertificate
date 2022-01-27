package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.manager.TestDatabaseManager;
import com.epam.esm.gcs.model.GiftCertificateModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({ "dev" })
@ContextConfiguration(locations = { "/test-config.xml" })
class GiftCertificateRepositoryImplTest {

    private final GiftCertificateRepositoryImpl certificateRepository;
    private final TestDatabaseManager databaseManager;

    @Autowired
    public GiftCertificateRepositoryImplTest(
            GiftCertificateRepositoryImpl certificateRepository,
            TestDatabaseManager databaseManager) {
        this.certificateRepository = certificateRepository;
        this.databaseManager = databaseManager;
    }

    @BeforeEach
    public void refreshTables() throws SQLException {
        databaseManager.dropCreateAndPopulateTables();
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final GiftCertificateModel certificate = getTestGiftCertificate();
        final GiftCertificateModel createdCertificate = certificateRepository.create(certificate);
        certificate.setId(createdCertificate.getId());

        assertEquals(certificate, createdCertificate);
        assertTrue(createdCertificate.getId().compareTo(0L) > 0);
    }

    @Test
    void create_shouldThrowEntityNotFoundException_ifNameIsNotUnique() {
        final GiftCertificateModel certificate = getTestGiftCertificate();

        certificateRepository.create(certificate);

        assertThrows(DuplicateKeyException.class, () ->
                certificateRepository.create(certificate)
        );
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        final GiftCertificateModel certificate = certificateRepository.create(getTestGiftCertificate());

        Optional<GiftCertificateModel> actualCertificate =
                certificateRepository.findById(certificate.getId());

        assertTrue(actualCertificate.isPresent());
        GiftCertificateModel actual = actualCertificate.get();
        assertEquals(certificate.getName(), actual.getName());
        assertEquals(certificate.getDescription(), actual.getDescription());
        assertEquals(0, certificate.getPrice().compareTo(actual.getPrice()));
        assertEquals(certificate.getDuration(), actual.getDuration());
        assertEquals(certificate.getTags(), actual.getTags());
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifNotExistsWithId() {
        Optional<GiftCertificateModel> actualCertificate = certificateRepository.findById(100000L);

        assertTrue(actualCertificate.isEmpty());
    }

    @Test
    void findAll_shouldReturnListOfTags_always() {
        final List<GiftCertificateModel> certificates = certificateRepository.findAll();

        assertNotNull(certificates);
        assertEquals(3, certificates.size());
    }

    @Test
    void delete_shouldReturnTrue_whenIsDeleted() {
        final GiftCertificateModel certificate = certificateRepository.create(getTestGiftCertificate());
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
        final GiftCertificateModel certificate = certificateRepository.create(getTestGiftCertificate());

        Optional<GiftCertificateModel> actualCertificate =
                certificateRepository.findByName(certificate.getName());

        assertTrue(actualCertificate.isPresent());
        GiftCertificateModel actual = actualCertificate.get();
        assertEquals(certificate.getName(), actual.getName());
        assertEquals(certificate.getDescription(), actual.getDescription());
        assertEquals(0, certificate.getPrice().compareTo(actual.getPrice()));
        assertEquals(certificate.getDuration(), actual.getDuration());
        assertEquals(certificate.getTags(), actual.getTags());
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotExistsWithName() {
        Optional<GiftCertificateModel> actualCertificate = certificateRepository.findByName("testName");

        assertTrue(actualCertificate.isEmpty());
    }

    @Test
    void update_shouldUpdateCertificate_whenExistsWithId() {
        final GiftCertificateModel certificate = certificateRepository.create(getTestGiftCertificate());
        final GiftCertificateModel toUpdate =
                GiftCertificateModel.builder()
                                    .id(certificate.getId())
                                    .name("testName")
                                    .description("newDescription")
                                    .price(BigDecimal.ONE)
                                    .duration(1)
                                    .build();

        Optional<GiftCertificateModel> oldCertOptional = certificateRepository.update(toUpdate);
        Optional<GiftCertificateModel> updatedCertOptional = certificateRepository.findById(certificate.getId());

        assertFalse(oldCertOptional.isEmpty());
        assertFalse(updatedCertOptional.isEmpty());
        GiftCertificateModel updCertificate = updatedCertOptional.get();
        GiftCertificateModel oldCertificate = oldCertOptional.get();
        assertEquals(certificate.getName(), oldCertificate.getName());
        assertEquals(certificate.getDescription(), oldCertificate.getDescription());
        assertEquals(certificate.getDuration(), oldCertificate.getDuration());
        assertEquals(0, certificate.getPrice().compareTo(oldCertificate.getPrice()));
        assertEquals(toUpdate.getName(), updCertificate.getName());
        assertEquals(toUpdate.getDescription(), updCertificate.getDescription());
        assertEquals(toUpdate.getDuration(), updCertificate.getDuration());
        assertEquals(0, toUpdate.getPrice().compareTo(updCertificate.getPrice()));
        assertEquals(-1, certificate.getLastUpdateDate().compareTo(updCertificate.getLastUpdateDate()));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenNotExistsWithId() {
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
        certificateRepository.create(getTestGiftCertificate());

        assertTrue(certificateRepository.existsWithName("testName"));
    }

    @Test
    void existsWithName_shouldReturnFalse_ifSuchRowNotExists() {
        assertFalse(certificateRepository.existsWithName("testName"));
    }

    private GiftCertificateModel getTestGiftCertificate() {
        return GiftCertificateModel.builder()
                                   .name("testName")
                                   .description("testDescription")
                                   .price(BigDecimal.TEN)
                                   .duration(10)
                                   .tags(new ArrayList<>())
                                   .build();
    }

}