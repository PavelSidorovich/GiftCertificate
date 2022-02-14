package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.config.ModelMapperConfig;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.FieldUpdateException;
import com.epam.esm.gcs.exception.NoFieldToUpdateException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.service.TagService;
import com.epam.esm.gcs.util.EntityFieldService;
import com.epam.esm.gcs.util.Limiter;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    private final GiftCertificateServiceImpl certificateService;

    private final GiftCertificateRepository certificateRepository;
    private final TagService tagService;
    private final ModelMapper modelMapper;
    private final EntityFieldService entityFieldService;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public GiftCertificateServiceImplTest(@Mock GiftCertificateRepository certificateRepository,
                                          @Mock TagService tagService,
                                          @Mock EntityFieldService entityFieldService) {
        this.tagService = tagService;
        this.certificateRepository = certificateRepository;
        this.entityFieldService = entityFieldService;
        this.modelMapper = new ModelMapperConfig().modelMapper();
        this.certificateService = new GiftCertificateServiceImpl(
                certificateRepository, tagService, entityFieldService, modelMapper
        );
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final String certificateName = "testName";
        final String tagName1 = "tag1";
        final String tagName2 = "tag2";
        final GiftCertificateDto certificateDto = getCertificateDtoToCreate(certificateName);
        final GiftCertificateModel certificateModel = getCreatedGiftCertificateModel(certificateName);
        final GiftCertificateDto expected = modelMapper.map(certificateModel, GiftCertificateDto.class);

        when(certificateRepository.existsWithName(certificateName)).thenReturn(false);
        when(tagService.existsWithName(tagName1)).thenReturn(true);
        when(tagService.existsWithName(tagName2)).thenReturn(true);
        when(tagService.findByName(tagName1)).thenReturn(new TagDto(1L, tagName1));
        when(tagService.findByName(tagName2)).thenReturn(new TagDto(2L, tagName2));
        certificateDto.setTags(List.of(new TagDto(1L, tagName1), new TagDto(2L, tagName2)));
        when(certificateRepository.create(mapCertificateToModel(certificateDto)))
                .thenReturn(certificateModel);

        assertEquals(expected, certificateService.create(certificateDto));
        verify(certificateRepository).flushAndClear();
    }

    @Test
    void create_shouldThrowDuplicatePropertyException_ifNameIsNotUnique() {
        final String certificateName = "testName";
        final GiftCertificateDto certificate = getCertificateDtoToCreate(certificateName);
        when(certificateRepository.existsWithName(certificateName)).thenReturn(true);

        assertThrows(DuplicatePropertyException.class, () -> certificateService.create(certificate));
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        final long certificateId = 1L;
        final String certificateName = "testName";
        final GiftCertificateDto expected = getCreatedCertificateDto(certificateName);
        final GiftCertificateModel certificateModel = mapCertificateToModel(expected);

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.ofNullable(certificateModel));

        assertEquals(expected, certificateService.findById(certificateId));
        verify(certificateRepository).findById(certificateId);
        verify(certificateRepository).clear();
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_ifNotExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.findById(certificateId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
        verify(certificateRepository).findById(certificateId);
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void findByName_shouldReturnCertificateModel_ifExistsWithName() {
        final String certificateName = "testName";
        final GiftCertificateDto expected = getCreatedCertificateDto(certificateName);
        final GiftCertificateModel certificateModel = mapCertificateToModel(expected);

        when(certificateRepository.findByName(certificateName)).thenReturn(Optional.ofNullable(certificateModel));

        assertEquals(expected, certificateService.findByName(certificateName));
        verify(certificateRepository).findByName(certificateName);
        verify(certificateRepository).clear();
    }

    @Test
    void findByName_shouldThrowEntityNotFoundException_ifNotExistsWithName() {
        final String certificateName = "testName";

        when(certificateRepository.findByName(certificateName)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> certificateService.findByName(certificateName));
        verify(certificateRepository).findByName(certificateName);
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void findAll_shouldReturnListOfCertificates_always() {
        final String certificateName = "testName";
        List<GiftCertificateDto> expected = List.of(getCreatedCertificateDto(certificateName));
        List<GiftCertificateModel> certificateModels = mapCertificatesToModels(expected);
        Limiter limiter = new QueryLimiter(10, 0);

        when(certificateRepository.findAll(limiter)).thenReturn(certificateModels);

        assertEquals(expected, certificateService.findAll(limiter));
        verify(certificateRepository).findAll(limiter);
        verify(certificateRepository).clear();
    }

    @Test
    void delete_shouldDeleteCertificate_ifExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.delete(certificateId)).thenReturn(true);

        certificateService.delete(certificateId);
        verify(certificateRepository).flushAndClear();
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_ifNotExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.delete(certificateId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void update_shouldReturnUpdatedCertificate_ifCertificateWasUpdated() {
        final String certificateName = "testName";
        final String tagName1 = "tag1";
        final String tagName2 = "tag2";
        final GiftCertificateDto beforeUpdateDto = getCreatedCertificateDto(certificateName);
        final GiftCertificateModel beforeUpdateModel = mapCertificateToModel(beforeUpdateDto);
        final GiftCertificateDto expected = getUpdatedCertificateDto(certificateName);
        final GiftCertificateModel updatedModel = mapCertificateToModel(expected);

        when(certificateRepository.findByName(certificateName))
                .thenReturn(Optional.of(beforeUpdateModel));
        when(tagService.existsWithName(tagName1)).thenReturn(true);
        when(tagService.existsWithName(tagName2)).thenReturn(true);
        when(tagService.findByName(tagName1)).thenReturn(new TagDto(1L, tagName1));
        when(tagService.findByName(tagName2)).thenReturn(new TagDto(2L, tagName2));
        beforeUpdateModel.setTags(List.of(new TagModel(1L, tagName1), new TagModel(2L, tagName2)));
        when(certificateRepository.update(beforeUpdateModel))
                .thenReturn(Optional.of(updatedModel));
        when(entityFieldService.getNotNullFields(beforeUpdateDto, "date", "name", "id"))
                .thenReturn(List.of("price"));

        assertEquals(expected, certificateService.update(beforeUpdateDto));
        verify(certificateRepository).flushAndClear();
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenCertificateWithIdNotFound() {
        final String certificateName = "testName";
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto(certificateName);

        when(certificateRepository.findByName(certificateName)).thenReturn(Optional.empty());
        when(certificateRepository.update(getCertificateModelToUpdate()))
                .thenReturn(Optional.empty());
        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(List.of("price"));

        assertThrows(EntityNotFoundException.class, () -> certificateService.update(beforeUpdate));
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void update_shouldThrowFieldUpdateException_whenCertificateContainsMoreThanOneFieldToUpdate() {
        final String certificateName = "testName";
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto(certificateName);

        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(List.of("description", "price", "duration"));

        assertThrows(FieldUpdateException.class, () -> certificateService.update(beforeUpdate));
        verify(certificateRepository, times(0)).flushAndClear();
    }

    @Test
    void update_shouldThrowNoFieldToUpdateException_whenCertificateHasNoFieldToUpdate() {
        final String certificateName = "testName";
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto(certificateName);

        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(Collections.emptyList());

        assertThrows(NoFieldToUpdateException.class, () -> certificateService.update(beforeUpdate));
        verify(certificateRepository, times(0)).flushAndClear();
    }

    private List<GiftCertificateModel> mapCertificatesToModels(List<GiftCertificateDto> certificates) {
        return certificates.stream()
                           .map(certificate -> modelMapper.map(certificate, GiftCertificateModel.class))
                           .collect(Collectors.toList());
    }

    private GiftCertificateModel mapCertificateToModel(GiftCertificateDto certificate) {
        return modelMapper.map(certificate, GiftCertificateModel.class);
    }

    private GiftCertificateDto getCertificateDtoToCreate(String name) {
        return GiftCertificateDto.builder()
                                 .name(name)
                                 .description("testDescription")
                                 .price(new BigDecimal("10.00"))
                                 .duration(10)
                                 .tags(List.of(
                                         new TagDto(null, "tag1"),
                                         new TagDto(null, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateModel getCreatedGiftCertificateModel(String name) {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name(name)
                                   .description("testDescription")
                                   .price(new BigDecimal("10.00"))
                                   .duration(10)
                                   .createDate(dateTime)
                                   .lastUpdateDate(dateTime)
                                   .tags(List.of(
                                           new TagModel(1L, "tag1"),
                                           new TagModel(2L, "tag2")
                                   ))
                                   .build();
    }

    private GiftCertificateDto getCreatedCertificateDto(String name) {
        return GiftCertificateDto.builder()
                                 .id(1L)
                                 .name(name)
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

    private GiftCertificateDto getUpdatedCertificateDto(String name) {
        return GiftCertificateDto.builder()
                                 .id(1L)
                                 .name(name)
                                 .description("")
                                 .price(new BigDecimal("6.00"))
                                 .duration(1)
                                 .createDate(dateTime)
                                 .lastUpdateDate(dateTime)
                                 .tags(List.of(
                                         new TagDto(1L, "tag1"),
                                         new TagDto(2L, "tag2")
                                 ))
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

}