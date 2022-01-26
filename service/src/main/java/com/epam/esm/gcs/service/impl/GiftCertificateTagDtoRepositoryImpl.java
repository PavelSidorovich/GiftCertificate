package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.comparator.ComparatorType;
import com.epam.esm.gcs.comparator.GiftCertificateComparatorBuilder;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateTagRepository;
import com.epam.esm.gcs.service.GiftCertificateTagDtoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.esm.gcs.repository.mapper.GiftCertificateColumn.*;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateTagDtoRepositoryImpl implements GiftCertificateTagDtoRepository {


    private final GiftCertificateTagRepository linker;
    private final ModelMapper modelMapper;
    private final GiftCertificateComparatorBuilder certificateComparatorBuilder;

    @Override
    public void link(GiftCertificateDto model) {
        linker.link(modelMapper.map(model, GiftCertificateModel.class));
    }

    @Override
    public void unlink(GiftCertificateDto model) {
        linker.unlink(modelMapper.map(model, GiftCertificateModel.class));
    }

    @Override
    public GiftCertificateDto findById(long id) {
        return modelMapper.map(linker.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, ID.getColumnName(), id)
        ), GiftCertificateDto.class);
    }

    @Override
    public GiftCertificateDto findByName(String name) {
        return modelMapper.map(linker.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, NAME.getColumnName(), name)
        ), GiftCertificateDto.class);
    }

    @Override
    public List<GiftCertificateDto> findByFilter(GiftCertificateDto certificate,
                                                 String sortByCreatedDate, String sortByName) {
        Map<ComparatorType, String> sortByType = new LinkedHashMap<>();
        sortByType.put(ComparatorType.CREATE_DATE_COMPARATOR, sortByCreatedDate);
        sortByType.put(ComparatorType.NAME_COMPARATOR, sortByName);
        Comparator<GiftCertificateDto> comparator = certificateComparatorBuilder.buildComparator(sortByType);
        Stream<GiftCertificateDto> idStream =
                linker.findIdsByFilter(modelMapper.map(certificate, GiftCertificateModel.class)).stream()
                      .map(this::findById);

        return comparator != null
                ? idStream.sorted(comparator).collect(Collectors.toList())
                : idStream.collect(Collectors.toList());
    }

    @Override
    public List<GiftCertificateDto> findAll() {
        return linker.findAll().stream()
                     .map(certificate -> modelMapper.map(certificate, GiftCertificateDto.class))
                     .collect(Collectors.toList());
    }

}
