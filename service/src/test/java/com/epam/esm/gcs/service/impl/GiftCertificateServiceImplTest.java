package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.config.ModelMapperConfig;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.service.GiftCertificateTagService;
import com.epam.esm.gcs.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    private final GiftCertificateServiceImpl certificateService;

    private final TagService tagService;
    private final GiftCertificateRepository certificateRepository;
    private final GiftCertificateTagService certificateTagRepository;
    private final ModelMapper modelMapper;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public GiftCertificateServiceImplTest(@Mock GiftCertificateRepository certificateRepository,
                                          @Mock GiftCertificateTagService certificateTagRepository,
                                          @Mock TagService tagService) {
        this.tagService = tagService;
        this.certificateRepository = certificateRepository;
        this.certificateTagRepository = certificateTagRepository;
        this.modelMapper = new ModelMapperConfig().modelMapper();
        this.certificateService = new GiftCertificateServiceImpl(tagService, certificateTagRepository,
                                                                 certificateRepository, modelMapper);
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final String certificateName = "testName";
        final GiftCertificateModel certificateModel = getTestGiftCertificateModel();
        final GiftCertificateDto certificateDto = getCertificateDto();
        when(certificateRepository.existsWithName(certificateName)).thenReturn(false);
        when(tagService.existsWithName("tag1")).thenReturn(true);
        when(tagService.existsWithName("tag2")).thenReturn(true);
        when(tagService.findByName("tag1")).thenReturn(new TagDto(1L, "tag1"));
        when(tagService.findByName("tag2")).thenReturn(new TagDto(2L, "tag2"));
        when(certificateRepository.create(modelMapper.map(certificateDto, GiftCertificateModel.class)))
                .thenReturn(certificateModel);
        certificateModel.setTags(List.of(new TagModel(1L, "tag1"), new TagModel(2L, "tag2")));
        when(certificateService.findById(1L)).thenReturn(getCreatedCertificateDto());

        assertEquals(modelMapper.map(certificateModel, GiftCertificateDto.class),
                     certificateService.create(certificateDto));
    }

    @Test
    void create_shouldThrowDuplicatePropertyException_ifNameIsNotUnique() {
        final GiftCertificateDto certificate = getCertificateDto();
        when(certificateRepository.existsWithName("testName")).thenReturn(true);

        assertThrows(DuplicatePropertyException.class, () -> certificateService.create(certificate));
    }

    @Test
    void findByName_shouldReturnCertificateModel_ifExistsWithName() {
        final GiftCertificateDto certificateDto = getCreatedCertificateDto();
        final String certificateName = "testName";

        when(certificateTagRepository.findByName(certificateName)).thenReturn(certificateDto);

        assertEquals(certificateService.findByName(certificateName), certificateDto);
        verify(certificateTagRepository).findByName(certificateName);
    }

    @Test
    void findByName_shouldThrowEntityNotFoundException_ifNotExistsWithName() {
        final String certificateName = "testName";

        when(certificateTagRepository.findByName(certificateName)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> certificateService.findByName(certificateName));
        verify(certificateTagRepository).findByName(certificateName);
    }

    @Test
    void findAll_shouldReturnListOfCertificates_always() {
        List<GiftCertificateDto> certificates = List.of(getCreatedCertificateDto());

        when(certificateTagRepository.findAll()).thenReturn(certificates);

        assertEquals(certificates, certificateService.findAll());
        verify(certificateTagRepository).findAll();
    }

    @Test
    void delete_shouldDeleteCertificate_ifExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.delete(certificateId)).thenReturn(true);

        certificateService.delete(certificateId);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_ifNotExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.delete(certificateId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));
    }

    @Test
    void update_shouldReturnOldModel_ifFoundAndUpdatedModel() {
        final GiftCertificateDto oldCertificate = getCreatedCertificateDto();

        when(certificateTagRepository.findByName("testName")).thenReturn(oldCertificate);
        when(certificateRepository.update(getCertificateModelToUpdate()))
                .thenReturn(Optional.of(getUpdatedCertificateModel()));

        assertEquals(oldCertificate, certificateService.update(getCertificateDtoToUpdate()));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenCertificateWithIdNotFound() {
        final GiftCertificateDto oldCertificate = getCreatedCertificateDto();

        when(certificateTagRepository.findByName("testName")).thenReturn(oldCertificate);
        when(certificateRepository.update(getCertificateModelToUpdate()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.update(getCertificateDtoToUpdate()));
    }

    private GiftCertificateDto getCertificateDto() {
        return GiftCertificateDto.builder()
                                 .name("testName")
                                 .description("testDescription")
                                 .price(new BigDecimal("10.00"))
                                 .duration(10)
                                 .tags(List.of(
                                         new TagDto(null, "tag1"),
                                         new TagDto(null, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateDto getCreatedCertificateDto() {
        return GiftCertificateDto.builder()
                                 .id(1L)
                                 .name("testName")
                                 .description("testDescription")
                                 .price(new BigDecimal("10.00"))
                                 .duration(10)
                                 .createDate(dateTime)
                                 .lastUpdateDate(dateTime)
                                 .tags(List.of(
                                         new TagDto(1L, "tag1"),
                                         new TagDto(2L, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateDto getCertificateDtoToUpdate() {
        return GiftCertificateDto.builder()
                                 .name("testName")
                                 .description("")
                                 .price(new BigDecimal("6.00"))
                                 .duration(1)
                                 .tags(new ArrayList<>())
                                 .build();
    }

    private GiftCertificateModel getTestGiftCertificateModel() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("testName")
                                   .description("testDescription")
                                   .price(new BigDecimal("10.00"))
                                   .duration(10)
                                   .createDate(dateTime)
                                   .lastUpdateDate(dateTime)
                                   .build();
    }

    private GiftCertificateModel getCertificateModelToUpdate() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("testName")
                                   .description("")
                                   .price(new BigDecimal("6.00"))
                                   .duration(1)
                                   .tags(new ArrayList<>())
                                   .build();
    }

    private GiftCertificateModel getUpdatedCertificateModel() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("testName")
                                   .description("")
                                   .price(new BigDecimal("6.00"))
                                   .duration(1)
                                   .createDate(dateTime)
                                   .lastUpdateDate(dateTime)
                                   .tags(new ArrayList<>())
                                   .build();
    }

}