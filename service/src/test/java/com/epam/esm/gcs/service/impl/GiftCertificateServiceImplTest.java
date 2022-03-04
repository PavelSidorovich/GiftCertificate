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
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import com.epam.esm.gcs.spec.impl.CertificateCriteriaToSpecificationConverter;
import com.epam.esm.gcs.util.EntityFieldService;
import com.epam.esm.gcs.util.EntityMapper;
import com.epam.esm.gcs.util.impl.EntityMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableAutoConfiguration
class GiftCertificateServiceImplTest {

    private final GiftCertificateServiceImpl certificateService;

    private final GiftCertificateRepository certificateRepository;
    private final TagService tagService;
    private final EntityMapper modelMapper;
    private final EntityFieldService entityFieldService;
    private final ModelMapper certificateUpdateMapper;
    private final CertificateCriteriaToSpecificationConverter converter;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public GiftCertificateServiceImplTest(
            @Mock GiftCertificateRepository certificateRepository,
            @Mock TagService tagService,
            @Mock EntityFieldService entityFieldService,
            @Mock CertificateCriteriaToSpecificationConverter converter,
            @Mock ModelMapper certificateUpdateMapper) {
        this.tagService = tagService;
        this.certificateRepository = certificateRepository;
        this.entityFieldService = entityFieldService;
        this.certificateUpdateMapper = certificateUpdateMapper;
        this.converter = converter;
        this.modelMapper = new EntityMapperImpl(new ModelMapperConfig().modelMapper());
        this.certificateService = new GiftCertificateServiceImpl(
                certificateRepository, tagService, modelMapper,
                certificateUpdateMapper, entityFieldService, converter
        );
    }

    @Test
    void create_shouldReturnCreatedCertificate_ifNameIsUnique() {
        final String certificateName = "testName";
        final String tagName1 = "tag1";
        final String tagName2 = "tag2";
        final GiftCertificateDto certificateDto = getCertificateDtoToCreate();
        final GiftCertificateModel certificateModel = getCreatedGiftCertificateModel();
        final GiftCertificateDto expected = modelMapper.map(certificateModel, GiftCertificateDto.class);

        when(certificateRepository.existsByNameIgnoreCase(certificateName)).thenReturn(false);
        when(tagService.existsWithName(tagName1)).thenReturn(true);
        when(tagService.existsWithName(tagName2)).thenReturn(true);
        when(tagService.findByName(tagName1)).thenReturn(new TagDto(1L, tagName1));
        when(tagService.findByName(tagName2)).thenReturn(new TagDto(2L, tagName2));
        certificateDto.setTags(Set.of(new TagDto(1L, tagName1), new TagDto(2L, tagName2)));
        GiftCertificateModel certToCreate = mapCertificateToModel(certificateDto);
        certToCreate.setTags(Set.of(new TagModel(1L, tagName1), new TagModel(2L, tagName2)));
        when(certificateRepository.save(certToCreate))
                .thenReturn(certificateModel);

        assertEquals(expected, certificateService.create(certificateDto));
    }

    @Test
    void create_shouldThrowDuplicatePropertyException_ifNameIsNotUnique() {
        final String certificateName = "testName";
        final GiftCertificateDto certificate = getCertificateDtoToCreate();
        when(certificateRepository.existsByNameIgnoreCase(certificateName)).thenReturn(true);

        assertThrows(DuplicatePropertyException.class, () -> certificateService.create(certificate));
    }

    @Test
    void findById_shouldReturnCertificateModel_ifExistsWithId() {
        final long certificateId = 1L;
        final GiftCertificateDto expected = getCreatedCertificateDto();
        final GiftCertificateModel certificateModel = mapCertificateToModel(expected);

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.ofNullable(certificateModel));

        assertEquals(expected, certificateService.findById(certificateId));
        verify(certificateRepository).findById(certificateId);
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_ifNotExistsWithId() {
        final long certificateId = 1L;

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
        verify(certificateRepository).findById(certificateId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByFilter_shouldReturnListOfCertificates_whenSatisfyFilter() {
        final Page<GiftCertificateModel> page = (Page<GiftCertificateModel>) mock(Page.class);
        final PageRequest pageable = PageRequest.of(0, 10);
        final CertificateSearchCriteria searchCriteria = new CertificateSearchCriteria(
                pageable, "testName", null, null, null, null
        );
        final List<GiftCertificateDto> expected = List.of(getCreatedCertificateDto());
        final List<GiftCertificateModel> certificateModels = mapCertificatesToModels(expected);
        final Specification<GiftCertificateModel> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "testName");

        when(converter.byCriteria(searchCriteria, GiftCertificateModel.class)).thenReturn(
                Pair.of(specification, pageable)
        );
        when(page.getContent()).thenReturn(certificateModels);
        when(certificateRepository.findAll(specification, pageable))
                .thenReturn(page);

        assertEquals(expected, certificateService.findByFilter(searchCriteria));
        verify(converter).byCriteria(searchCriteria, GiftCertificateModel.class);
        verify(certificateRepository).findAll(specification, pageable);
    }

    @Test
    void findByTags_shouldReturnCertificateModel_ifExistsWithName() {
        List<GiftCertificateDto> expected = List.of(getCreatedCertificateDto());
        List<GiftCertificateModel> certificateModels = mapCertificatesToModels(expected);
        final List<String> tags = List.of("tag1", "tag2");
        final Pageable pageable = PageRequest.of(0, 10);

        when(certificateRepository.findByTags(tags, tags.size(), pageable))
                .thenReturn(certificateModels);

        assertEquals(expected, certificateService.findByTags(tags, pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnListOfCertificates_always() {
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<GiftCertificateModel> page = (Page<GiftCertificateModel>) mock(Page.class);
        List<GiftCertificateDto> expected = List.of(getCreatedCertificateDto());
        List<GiftCertificateModel> certificateModels = mapCertificatesToModels(expected);

        when(page.getContent()).thenReturn(certificateModels);
        when(certificateRepository.findAll(pageable)).thenReturn(page);

        assertEquals(expected, certificateService.findAll(pageable));
        verify(certificateRepository).findAll(pageable);
    }

    @Test
    void delete_shouldDeleteCertificate_ifExistsWithId() {
        final long certificateId = 1L;

        certificateService.delete(certificateId);

        verify(certificateRepository).deleteById(certificateId);
    }

    @Test
    void update_shouldReturnUpdatedCertificate_ifCertificateWasUpdated() {
        final String tagName1 = "tag1";
        final String tagName2 = "tag2";
        final GiftCertificateDto beforeUpdateDto = getCreatedCertificateDto();
        final GiftCertificateModel beforeUpdateModel = mapCertificateToModel(beforeUpdateDto);
        final GiftCertificateDto expected = getUpdatedCertificateDto();
        final GiftCertificateModel updatedModel = mapCertificateToModel(expected);

        when(entityFieldService.getNotNullFields(beforeUpdateDto, "date", "name", "id"))
                .thenReturn(List.of("price"));
        when(certificateRepository.findById(beforeUpdateDto.getId()))
                .thenReturn(Optional.of(beforeUpdateModel));
        when(tagService.existsWithName(tagName1)).thenReturn(true);
        when(tagService.existsWithName(tagName2)).thenReturn(true);
        when(tagService.findByName(tagName1)).thenReturn(new TagDto(1L, tagName1));
        when(tagService.findByName(tagName2)).thenReturn(new TagDto(2L, tagName2));
        beforeUpdateModel.setTags(Set.of(new TagModel(1L, tagName1), new TagModel(2L, tagName2)));
        when(certificateRepository.save(beforeUpdateModel))
                .thenReturn(updatedModel);

        assertEquals(expected, certificateService.update(beforeUpdateDto));
        verify(certificateUpdateMapper).map(beforeUpdateDto, beforeUpdateModel);
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenCertificateWithIdNotFound() {
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto();

        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(List.of("price"));
        when(certificateRepository.findById(beforeUpdate.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.update(beforeUpdate));
    }

    @Test
    void update_shouldThrowFieldUpdateException_whenCertificateContainsMoreThanOneFieldToUpdate() {
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto();

        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(List.of("description", "price", "duration"));

        assertThrows(FieldUpdateException.class, () -> certificateService.update(beforeUpdate));
    }

    @Test
    void update_shouldThrowNoFieldToUpdateException_whenCertificateHasNoFieldToUpdate() {
        final GiftCertificateDto beforeUpdate = getCreatedCertificateDto();

        when(entityFieldService.getNotNullFields(beforeUpdate, "date", "name", "id"))
                .thenReturn(Collections.emptyList());

        assertThrows(NoFieldToUpdateException.class, () -> certificateService.update(beforeUpdate));
    }

    private List<GiftCertificateModel> mapCertificatesToModels(List<GiftCertificateDto> certificates) {
        return certificates.stream()
                           .map(certificate -> modelMapper.map(certificate, GiftCertificateModel.class))
                           .collect(Collectors.toList());
    }

    private GiftCertificateModel mapCertificateToModel(GiftCertificateDto certificate) {
        return modelMapper.map(certificate, GiftCertificateModel.class);
    }

    private GiftCertificateDto getCertificateDtoToCreate() {
        return GiftCertificateDto.builder()
                                 .name("testName")
                                 .description("testDescription")
                                 .price(new BigDecimal("10.00"))
                                 .duration(10)
                                 .tags(Set.of(
                                         new TagDto(null, "tag1"),
                                         new TagDto(null, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateModel getCreatedGiftCertificateModel() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("testName")
                                   .description("testDescription")
                                   .price(new BigDecimal("10.00"))
                                   .duration(10)
                                   .createDate(dateTime)
                                   .lastUpdateDate(dateTime)
                                   .tags(Set.of(
                                           new TagModel(1L, "tag1"),
                                           new TagModel(2L, "tag2")
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
                                 .tags(Set.of(
                                         new TagDto(1L, "tag1"),
                                         new TagDto(2L, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateDto getUpdatedCertificateDto() {
        return GiftCertificateDto.builder()
                                 .id(1L)
                                 .name("testName")
                                 .description("")
                                 .price(new BigDecimal("6.00"))
                                 .duration(1)
                                 .createDate(dateTime)
                                 .lastUpdateDate(dateTime)
                                 .tags(Set.of(
                                         new TagDto(1L, "tag1"),
                                         new TagDto(2L, "tag2")
                                 ))
                                 .build();
    }

}
