package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.comparator.GiftCertificateComparatorBuilder;
import com.epam.esm.gcs.config.ModelMapperConfig;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateTagServiceImplTest {

    private final GiftCertificateTagServiceImpl certificateTagDtoRepository;

    private final GiftCertificateTagRepository linker;
    private final ModelMapper modelMapper;
    private final LocalDateTime localDateTime = LocalDateTime.now();

    public GiftCertificateTagServiceImplTest(@Mock GiftCertificateTagRepository linker,
                                             @Mock GiftCertificateComparatorBuilder comparatorBuilder) {
        this.linker = linker;
        this.modelMapper = new ModelMapperConfig().modelMapper();
        this.certificateTagDtoRepository =
                new GiftCertificateTagServiceImpl(linker, modelMapper, comparatorBuilder);
    }

    @Test
    void link_shouldLinkTagsWithCertificate_always() {
        final GiftCertificateDto certificateDto = getCertificateDto();

        certificateTagDtoRepository.link(certificateDto);

        verify(linker).link(modelMapper.map(certificateDto, GiftCertificateModel.class));
    }

    @Test
    void unlink_shouldUnlinkTagsFromCertificate_always() {
        final GiftCertificateDto certificateDto = getCertificateDto();

        certificateTagDtoRepository.unlink(certificateDto);

        verify(linker).unlink(modelMapper.map(certificateDto, GiftCertificateModel.class));
    }

    @Test
    void findById_shouldReturnCertificate_whenExistsWithId() {
        final long certificateId = 1L;

        when(linker.findById(certificateId)).thenReturn(Optional.of(getCertificateModel()));

        assertEquals(getCertificateDto(), certificateTagDtoRepository.findById(certificateId));
        verify(linker).findById(certificateId);
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_whenNotExistsWithId() {
        final long certificateId = 1L;

        when(linker.findById(certificateId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateTagDtoRepository.findById(certificateId));
        verify(linker).findById(certificateId);
    }

    @Test
    void findByName_shouldReturnCertificate_whenExistsWithName() {
        final String certificateName = "testName";

        when(linker.findByName(certificateName)).thenReturn(Optional.of(getCertificateModel()));

        assertEquals(getCertificateDto(), certificateTagDtoRepository.findByName(certificateName));
        verify(linker).findByName(certificateName);
    }

    @Test
    void findByName_shouldThrowEntityNotFoundException_whenNotExistsWithName() {
        final String certificateName = "testName";

        when(linker.findByName(certificateName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateTagDtoRepository.findByName(certificateName));
        verify(linker).findByName(certificateName);
    }

    @Test
    void findAll_shouldReturnListOfCertificates_always() {
        final List<GiftCertificateModel> certificateModels = List.of(
                getCertificateModel(), getCertificateModel()
        );
        final List<GiftCertificateDto> certificateDtos = List.of(
                getCertificateDto(), getCertificateDto()
        );

        when(linker.findAll()).thenReturn(certificateModels);

        assertEquals(certificateDtos, certificateTagDtoRepository.findAll());
    }

    private GiftCertificateDto getCertificateDto() {
        return GiftCertificateDto.builder()
                                 .id(1L)
                                 .name("testName")
                                 .description("testDescription")
                                 .price(new BigDecimal("10.00"))
                                 .duration(10)
                                 .createDate(localDateTime)
                                 .lastUpdateDate(localDateTime)
                                 .tags(List.of(
                                         new TagDto(1L, "tag1"),
                                         new TagDto(2L, "tag2")
                                 ))
                                 .build();
    }

    private GiftCertificateModel getCertificateModel() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("testName")
                                   .description("testDescription")
                                   .price(new BigDecimal("10.00"))
                                   .duration(10)
                                   .createDate(localDateTime)
                                   .lastUpdateDate(localDateTime)
                                   .tags(List.of(
                                           new TagModel(1L, "tag1"),
                                           new TagModel(2L, "tag2")
                                   ))
                                   .build();
    }

}