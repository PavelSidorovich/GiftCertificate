package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.FieldUpdateException;
import com.epam.esm.gcs.exception.NoFieldToUpdateException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.TagService;
import com.epam.esm.gcs.util.EntityFieldService;
import com.epam.esm.gcs.util.Limiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.gcs.repository.column.GiftCertificateColumn.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private static final String DATE_NAMING_PART = "date";
    private static final String NAME_NAMING_PART = "name";
    private static final String ID_NAMING_PART = "id";

    private final GiftCertificateRepository certificateRepository;
    private final TagService tagService;
    private final EntityFieldService entityFieldService;
    private final ModelMapper modelMapper;

    /**
     * Creates new certificate as well as tags in it
     *
     * @param certificate certificate to create
     * @return created certificate with generated id
     * @throws DuplicatePropertyException if certificate with such name already exists
     */
    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto certificate) {
        final String certificateName = certificate.getName();

        if (certificateRepository.existsWithName(certificateName)) {
            throw new DuplicatePropertyException(
                    GiftCertificateDto.class, NAME.getColumnName(), certificateName
            );
        }
        certificate.setTags(prepareTagsToMerge(certificate.getTags()));
        GiftCertificateModel created = certificateRepository.create(
                modelMapper.map(certificate, GiftCertificateModel.class)
        );
        certificateRepository.flushAndClear();
        return modelMapper.map(created, GiftCertificateDto.class);
    }

    private List<TagDto> prepareTagsToMerge(List<TagDto> tags) {
        return tags == null? null
                : tags.stream()
                      .map(tag -> tagService.existsWithName(tag.getName())
                              ? tagService.findByName(tag.getName()) : tag)
                      .collect(Collectors.toList());
    }

    /**
     * Finds certificate (including tags) with provided id
     *
     * @param id id of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with specified id not found
     */
    @Override
    public GiftCertificateDto findById(long id) {
        GiftCertificateModel certificate = certificateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(GiftCertificateDto.class, ID.getColumnName(), id)
        );
        certificateRepository.clear();
        return modelMapper.map(certificate, GiftCertificateDto.class);
    }

    /**
     * Finds certificate (including tags) with provided name
     *
     * @param name name of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with specified name not found
     */
    @Override
    public GiftCertificateDto findByName(String name) {
        GiftCertificateDto certificate = findByNameWithoutClear(name);
        certificateRepository.clear();
        return certificate;
    }

    /**
     * Finds certificates satisfying filter
     *
     * @param certificate       entity-filter
     * @param sortByCreatedDate string value of sort type by date of creation (ASC or DESC)
     * @param sortByName        string value of sort type by name (ASC or DESC)
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateDto> findByFilter(GiftCertificateDto certificate,
                                                 String sortByCreatedDate, String sortByName) {
//        return linker.findByFilter(certificate, sortByCreatedDate, sortByName);
        return null;
    }

    /**
     * Finds all certificates (including tags)
     *
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateDto> findAll(Limiter limiter) {
        List<GiftCertificateDto> certificates =
                certificateRepository.findAll(limiter).stream()
                                     .map(certificate -> modelMapper.map(
                                             certificate,
                                             GiftCertificateDto.class))
                                     .collect(Collectors.toList());
        certificateRepository.clear();
        return certificates;
    }

    /**
     * Deletes certificate with specified id
     *
     * @param id id of certificate to delete
     * @throws EntityNotFoundException if certificate with specified id not found
     */
    @Override
    @Transactional
    public void delete(long id) {
        if (!certificateRepository.delete(id)) {
            throw new EntityNotFoundException(GiftCertificateDto.class, ID.getColumnName(), id);
        }
        certificateRepository.flushAndClear();
    }

    /**
     * Updates certificate with specified name
     *
     * @param model certificate to update. Should contain name<br>
     *              <b>Note:</b> only not null fields will be updated
     * @return updated certificate
     * @throws EntityNotFoundException  if certificate with specified name not found
     * @throws FieldUpdateException     if certificate contains more than one field to update (not null)
     * @throws NoFieldToUpdateException if certificate doesn't contain any field to update
     */
    @Override
    @Transactional
    public GiftCertificateDto update(GiftCertificateDto model) {
        countFieldsToEdit(model);
        model.setId(findByNameWithoutClear(model.getName()).getId());
        model.setTags(prepareTagsToMerge(model.getTags()));
        Optional<GiftCertificateModel> updated = certificateRepository.update(
                modelMapper.map(model, GiftCertificateModel.class)
        );
        certificateRepository.flushAndClear();
        return modelMapper.map(updated.orElseThrow(() -> new EntityNotFoundException(
                GiftCertificateDto.class, NAME.getColumnName(), model.getName())
        ), GiftCertificateDto.class);
    }

    private GiftCertificateDto findByNameWithoutClear(String name) {
        return modelMapper.map(certificateRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(GiftCertificateDto.class, NAME.getColumnName(), name)
        ), GiftCertificateDto.class);
    }

    private void countFieldsToEdit(GiftCertificateDto model) {
        List<String> fields = entityFieldService.getNotNullFields(
                model, DATE_NAMING_PART, NAME_NAMING_PART, ID_NAMING_PART
        );
        final int fieldsSize = fields.size();

        if (fieldsSize > 1) {
            throw new FieldUpdateException(GiftCertificateDto.class, fields);
        } else if (fieldsSize < 1) {
            throw new NoFieldToUpdateException(GiftCertificateDto.class);
        }
    }

}
