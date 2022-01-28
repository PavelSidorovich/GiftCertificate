package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.comparator.ComparatorType;
import com.epam.esm.gcs.comparator.GiftCertificateComparatorBuilder;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateTagRepository;
import com.epam.esm.gcs.service.GiftCertificateTagService;
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
public class GiftCertificateTagServiceImpl implements GiftCertificateTagService {

    private final GiftCertificateTagRepository linker;
    private final ModelMapper modelMapper;
    private final GiftCertificateComparatorBuilder certificateComparatorBuilder;

    /**
     * Creates rows linking certificate entity with tags in database<br>
     * <strong>Note:</strong> certificate and tags should have ids
     * @param model certificate with tags to link
     */
    @Override
    public void link(GiftCertificateDto model) {
        linker.link(modelMapper.map(model, GiftCertificateModel.class));
    }

    /**
     * Unlinks all tags from provided certificate<br>
     * <strong>Note:</strong> certificate should have id
     * @param model certificate to unlink from tags
     */
    @Override
    public void unlink(GiftCertificateDto model) {
        linker.unlink(modelMapper.map(model, GiftCertificateModel.class));
    }

    /**
     * Finds certificate (including tags) with provided id
     * @param id id of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with provided id not found
     */
    @Override
    public GiftCertificateDto findById(long id) {
        return modelMapper.map(linker.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, ID.getColumnName(), id)
        ), GiftCertificateDto.class);
    }

    /**
     * Finds certificate (including tags) with provided name
     * @param name id of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with provided name not found
     */
    @Override
    public GiftCertificateDto findByName(String name) {
        return modelMapper.map(linker.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, NAME.getColumnName(), name)
        ), GiftCertificateDto.class);
    }

    /**
     * Finds certificates satisfying filter
     * @param certificate entity-filter
     * @param sortByCreatedDate string value of sort type by date of creation (ASC or DESC)
     * @param sortByName string value of sort type by name (ASC or DESC)
     * @return list of certificates
     */
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

    /**
     * Finds all certificates (including tags)
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateDto> findAll() {
        return linker.findAll().stream()
                     .map(certificate -> modelMapper.map(certificate, GiftCertificateDto.class))
                     .collect(Collectors.toList());
    }

}
