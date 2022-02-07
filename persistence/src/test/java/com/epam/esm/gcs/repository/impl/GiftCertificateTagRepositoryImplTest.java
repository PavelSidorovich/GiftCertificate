package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class GiftCertificateTagRepositoryImplTest {

    private final GiftCertificateTagRepositoryImpl certificateTagRepository;

    @Autowired
    public GiftCertificateTagRepositoryImplTest(
            GiftCertificateTagRepositoryImpl certificateTagRepository) {
        this.certificateTagRepository = certificateTagRepository;
    }

    @Test
    void link_shouldLinkCertificateAndTags_always() {
        GiftCertificateModel certificate = certificateTagRepository.findAll().get(0);
        List<TagModel> tags = certificate.getTags();

        assertFalse(tags.isEmpty());
        certificateTagRepository.unlink(certificate);

        Optional<GiftCertificateModel> certificateOptional
                = certificateTagRepository.findById(certificate.getId());
        assertTrue(certificateOptional.isPresent());
        assertTrue(certificateOptional.get().getTags().isEmpty());

        certificateTagRepository.link(certificate);
        certificateOptional = certificateTagRepository.findById(certificate.getId());
        assertTrue(certificateOptional.isPresent());
        assertFalse(certificateOptional.get().getTags().isEmpty());
    }

    @Test
    void unlink_shouldUnlinkTagsFromCertificate_always() {
        GiftCertificateModel certificate = certificateTagRepository.findAll().get(0);
        List<TagModel> tags = certificate.getTags();

        assertFalse(tags.isEmpty());
        certificateTagRepository.unlink(certificate);

        Optional<GiftCertificateModel> certificateOptional
                = certificateTagRepository.findById(certificate.getId());
        assertTrue(certificateOptional.isPresent());
        assertTrue(certificateOptional.get().getTags().isEmpty());
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        Optional<GiftCertificateModel> actualCertificate =
                certificateTagRepository.findById(1L);
        Optional<GiftCertificateModel> actualCertificateCopy =
                certificateTagRepository.findById(1L);

        assertTrue(actualCertificate.isPresent());
        assertTrue(actualCertificateCopy.isPresent());
        assertEquals(actualCertificate.get(), actualCertificateCopy.get());
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifNotExistsWithId() {
        Optional<GiftCertificateModel> actualCertificate = certificateTagRepository.findById(100000L);

        assertTrue(actualCertificate.isEmpty());
    }

    @Test
    void findByName_shouldReturnCertificateModel_ifExistsWithName() {
        Optional<GiftCertificateModel> actualCertificate =
                certificateTagRepository.findByName("Sports Subscription");
        Optional<GiftCertificateModel> actualCertificateCopy =
                certificateTagRepository.findByName("Sports Subscription");

        assertTrue(actualCertificate.isPresent());
        assertTrue(actualCertificateCopy.isPresent());
        assertEquals(actualCertificate.get(), actualCertificateCopy.get());
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotExistsWithName() {
        Optional<GiftCertificateModel> actualCertificate
                = certificateTagRepository.findByName("invalidName");

        assertTrue(actualCertificate.isEmpty());
    }

    @Test
    void findAll_shouldReturnListOfCertificates_always() {
        List<GiftCertificateModel> certificates = certificateTagRepository.findAll();

        assertNotNull(certificates);
        assertSame(3, certificates.size());
    }

}