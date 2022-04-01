package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.FieldUpdateException;
import com.epam.esm.gcs.exception.NoFieldsToUpdateException;
import com.epam.esm.gcs.exception.WiredEntityDeletionException;
import com.epam.esm.gcs.model.CertificateModel;
import com.epam.esm.gcs.model.CertificateModel_;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.CertificateRepository;
import com.epam.esm.gcs.service.CertificateService;
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
public class CertificateServiceImpl implements CertificateService {

    private static final String DATE_NAMING_PART = "date";

    private final CertificateRepository certificateRepository;
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
    public CertificateDto create(CertificateDto certificate) {
        final String certificateName = certificate.getName();

        if (certificateRepository.existsByNameIgnoreCase(certificateName)) {
            throw new DuplicatePropertyException(
                    CertificateDto.class, CertificateModel_.NAME, certificateName
            );
        }
        CertificateModel certToCreate = entityMapper.map(certificate, CertificateModel.class);
        certToCreate.setTags(prepareTagsForCertificate(certificate.getTags()));

        return entityMapper.map(certificateRepository.save(certToCreate), CertificateDto.class);
    }

    /**
     * Finds certificate (including tags) with provided id
     *
     * @param id id of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with specified id not found
     */
    @Override
    public CertificateDto findById(long id) {
        CertificateModel certificate = certificateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(CertificateDto.class, CertificateModel_.ID, id)
        );
        return entityMapper.map(certificate, CertificateDto.class);
    }

    /**
     * Finds certificates satisfying filter
     *
     * @param searchCriteria filter to search by
     * @return list of certificates which satisfy applied filter
     */
    @Override
    public List<CertificateDto> findByFilter(CertificateSearchCriteria searchCriteria) {
        Pair<Specification<CertificateModel>, Pageable> pair =
                converter.byCriteria(searchCriteria, CertificateModel.class);
        return entityMapper.map(
                certificateRepository.findAll(pair.getFirst(), pair.getSecond()).getContent(),
                CertificateDto.class
        );
    }

    /**
     * Finds all certificates (including tags)
     *
     * @param pageable pagination
     * @return list of certificates
     */
    @Override
    public List<CertificateDto> findAll(Pageable pageable) {
        return entityMapper.map(
                certificateRepository.findAll(pageable).getContent(),
                CertificateDto.class
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
    public List<CertificateDto> findByTags(List<String> tags, Pageable pageable) {
        List<String> tagModels = tags.stream()
                                     .map(String::toLowerCase)
                                     .collect(Collectors.toList());
        return entityMapper.map(
                certificateRepository.findByTags(tagModels, pageable),
                CertificateDto.class
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
            throw new WiredEntityDeletionException(CertificateDto.class, CertificateModel_.ID, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(CertificateDto.class, CertificateModel_.ID, id);
        }
    }

    /**
     * Updates certificate with specified id
     *
     * @param certificateDto certificate to update. Should contain name<br>
     *                       <b>Note:</b> only not null fields will be updated
     * @return updated certificate
     * @throws EntityNotFoundException   if certificate with specified id not found
     * @throws FieldUpdateException      if certificate contains more than one field to update (not null)
     * @throws NoFieldsToUpdateException if certificate doesn't contain any field to update
     */
    @Override
    @Transactional
    public CertificateDto update(CertificateDto certificateDto) {
        countFieldsToEdit(certificateDto);
        Optional<CertificateModel> optCert = certificateRepository.findById(certificateDto.getId());
        CertificateModel certToUpdate = optCert.orElseThrow(() -> new EntityNotFoundException(
                CertificateDto.class, CertificateModel_.ID, certificateDto.getId())
        );
        Set<TagModel> preparedTags = prepareTagsForCertificate(certificateDto.getTags());
        certificateUpdateMapper.map(certificateDto, certToUpdate);
        if (preparedTags != null) {
            certToUpdate.setTags(preparedTags);
        }

        return entityMapper.map(certificateRepository.save(certToUpdate), CertificateDto.class);
    }

    private void countFieldsToEdit(CertificateDto model) {
        List<String> fields = entityFieldService.getNotNullFields(
                model, DATE_NAMING_PART,
                CertificateModel_.NAME,
                CertificateModel_.ID
        );
        final int fieldsSize = fields.size();

        if (fieldsSize < 1) {
            throw new NoFieldsToUpdateException(CertificateDto.class);
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
