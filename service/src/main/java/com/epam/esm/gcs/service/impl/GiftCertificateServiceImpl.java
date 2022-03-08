package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.FieldUpdateException;
import com.epam.esm.gcs.exception.NoFieldToUpdateException;
import com.epam.esm.gcs.exception.WiredEntityDeletionException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.GiftCertificateModel_;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.TagService;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import com.epam.esm.gcs.spec.impl.CertificateCriteriaToSpecificationConverter;
import com.epam.esm.gcs.util.EntityFieldService;
import com.epam.esm.gcs.util.EntityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private static final String DATE_NAMING_PART = "date";

    private final GiftCertificateRepository certificateRepository;
    private final TagService tagService;
    private final EntityMapper entityMapper;
    private final ModelMapper certificateUpdateMapper;
    private final EntityFieldService entityFieldService;
    private final CertificateCriteriaToSpecificationConverter converter;

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

        if (certificateRepository.existsByNameIgnoreCase(certificateName)) {
            throw new DuplicatePropertyException(
                    GiftCertificateDto.class, GiftCertificateModel_.NAME, certificateName
            );
        }
        GiftCertificateModel certToCreate = entityMapper.map(certificate, GiftCertificateModel.class);
        certToCreate.setTags(prepareTagsForCertificate(certificate.getTags()));

        return entityMapper.map(certificateRepository.save(certToCreate), GiftCertificateDto.class);
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
                () -> new EntityNotFoundException(GiftCertificateDto.class, GiftCertificateModel_.ID, id)
        );
        return entityMapper.map(certificate, GiftCertificateDto.class);
    }

    /**
     * Finds certificates satisfying filter
     *
     * @param searchCriteria filter to search by
     * @return list of certificates which satisfy applied filter
     */
    @Override
    public List<GiftCertificateDto> findByFilter(CertificateSearchCriteria searchCriteria) {
        Pair<Specification<GiftCertificateModel>, Pageable> pair =
                converter.byCriteria(searchCriteria, GiftCertificateModel.class);
        return entityMapper.map(
                certificateRepository.findAll(pair.getFirst(), pair.getSecond()).getContent(),
                GiftCertificateDto.class
        );
    }

    /**
     * Finds all certificates (including tags)
     *
     * @param pageable pagination
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateDto> findAll(Pageable pageable) {
        return entityMapper.map(
                certificateRepository.findAll(pageable).getContent(),
                GiftCertificateDto.class
        );
    }

    /**
     * Find certificates with provided tags
     *
     * @param tags     tags in certificate
     * @param pageable pagination
     * @return list of certificates with containing tags
     */
    @Override
    public List<GiftCertificateDto> findByTags(List<String> tags, Pageable pageable) {
        List<String> tagModels = tags.stream()
                                     .map(String::toLowerCase)
                                     .collect(Collectors.toList());
        return entityMapper.map(
                certificateRepository.findByTags(tagModels, tags.size(), pageable),
                GiftCertificateDto.class
        );
    }

    /**
     * Deletes certificate with specified id
     *
     * @param id id of certificate to delete
     */
    @Override
    public void delete(long id) {
        try {
            certificateRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new WiredEntityDeletionException(GiftCertificateDto.class, GiftCertificateModel_.ID, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(GiftCertificateDto.class, GiftCertificateModel_.ID, id);
        }
    }

    /**
     * Updates certificate with specified id
     *
     * @param certificateDto certificate to update. Should contain name<br>
     *                       <b>Note:</b> only not null fields will be updated
     * @return updated certificate
     * @throws EntityNotFoundException  if certificate with specified id not found
     * @throws FieldUpdateException     if certificate contains more than one field to update (not null)
     * @throws NoFieldToUpdateException if certificate doesn't contain any field to update
     */
    @Override
    @Transactional
    public GiftCertificateDto update(GiftCertificateDto certificateDto) {
        countFieldsToEdit(certificateDto);
        Optional<GiftCertificateModel> optCert = certificateRepository.findById(certificateDto.getId());
        GiftCertificateModel certToUpdate = optCert.orElseThrow(() -> new EntityNotFoundException(
                GiftCertificateDto.class, GiftCertificateModel_.ID, certificateDto.getId())
        );
        Set<TagModel> preparedTags = prepareTagsForCertificate(certificateDto.getTags());
        certificateUpdateMapper.map(certificateDto, certToUpdate);
        if (preparedTags != null) {
            certToUpdate.setTags(preparedTags);
        }

        return entityMapper.map(certificateRepository.save(certToUpdate), GiftCertificateDto.class);
    }

    private void countFieldsToEdit(GiftCertificateDto model) {
        List<String> fields = entityFieldService.getNotNullFields(
                model, DATE_NAMING_PART,
                GiftCertificateModel_.NAME,
                GiftCertificateModel_.ID
        );
        final int fieldsSize = fields.size();

        if (fieldsSize > 1) {
            throw new FieldUpdateException(GiftCertificateDto.class, fields);
        } else if (fieldsSize < 1) {
            throw new NoFieldToUpdateException(GiftCertificateDto.class);
        }
    }

    private Set<TagModel> prepareTagsForCertificate(Set<TagDto> tags) {
        return tags == null? null
                : tags.stream()
                      .map(tag -> entityMapper.map(
                              tagService.existsWithName(tag.getName())
                                      ? tagService.findByName(tag.getName())
                                      : tagService.create(tag), TagModel.class)
                      )
                      .collect(Collectors.toSet());
    }

}
