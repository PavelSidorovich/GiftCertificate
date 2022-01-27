package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.GiftCertificateTagDtoRepository;
import com.epam.esm.gcs.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.gcs.repository.mapper.GiftCertificateColumn.*;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private final TagService tagService;
    private final GiftCertificateTagDtoRepository linker;
    private final GiftCertificateRepository certificateRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates new certificate (including tags)
     * @param certificate certificate to create
     * @return created certificate with generated id
     * @throws DuplicatePropertyException if certificate with such name already exists
     */
    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto certificate) {
        final String certificateName = certificate.getName();

        if (certificateRepository.existsWithName(certificateName)) {
            throw new DuplicatePropertyException(GiftCertificateDto.class,
                                                 NAME.getColumnName(), certificateName
            );
        }
        certificate = createCertificateAndTags(certificate);
        linker.link(certificate);

        return findById(certificate.getId());
    }

    private GiftCertificateDto createCertificateAndTags(GiftCertificateDto certificate) {
        List<TagDto> certificateTags = createTagsIfNotExist(certificate.getTags());

        certificate = modelMapper.map(
                certificateRepository.create(modelMapper.map(certificate, GiftCertificateModel.class)),
                GiftCertificateDto.class
        );
        certificate.setTags(certificateTags);

        return certificate;
    }

    private List<TagDto> createTagsIfNotExist(List<TagDto> modelTags) {
        List<TagDto> tags = new ArrayList<>(modelTags);
        List<TagDto> tagsToCreate = tags.stream()
                                        .filter(tag -> !tagService.existsWithName(tag.getName()))
                                        .collect(Collectors.toList());
        tags.removeAll(tagsToCreate);
        List<TagDto> createdTags = tagsToCreate.stream()
                                               .map(tagService::create)
                                               .collect(Collectors.toList());
        List<TagDto> foundTags = tags.stream()
                                     .map(tag -> tagService.findByName(tag.getName()))
                                     .collect(Collectors.toList());
        createdTags.addAll(foundTags);
        return createdTags;
    }

    /**
     * Finds certificate (including tags) with provided id
     * @param id id of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with specified id not found
     */
    @Override
    public GiftCertificateDto findById(long id) {
        return linker.findById(id);
    }

    /**
     * Finds certificate (including tags) with provided name
     * @param name name of certificate to find
     * @return certificate if found
     * @throws EntityNotFoundException if certificate with specified name not found
     */
    @Override
    public GiftCertificateDto findByName(String name) {
        return linker.findByName(name);
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
        return linker.findByFilter(certificate, sortByCreatedDate, sortByName);
    }

    /**
     * Finds all certificates (including tags)
     * @return list of certificates
     */
    @Override
    public List<GiftCertificateDto> findAll() {
        return linker.findAll();
    }

    /**
     * Deletes certificate with specified id
     * @param id id of certificate to delete
     * @throws EntityNotFoundException if certificate with specified id not found
     */
    @Override
    public void delete(long id) {
        if (!certificateRepository.delete(id)) {
            throw new EntityNotFoundException(GiftCertificateDto.class, ID.getColumnName(), id);
        }
    }

    /**
     * Updates certificate with specified name
     * @param model certificate to update. Should contain name
     * @return old version of certificate
     * @throws EntityNotFoundException if certificate with specified name not found
     */
    @Transactional
    @Override
    public GiftCertificateDto update(GiftCertificateDto model) {
        GiftCertificateDto oldModel = linker.findByName(model.getName());
        List<TagDto> tags = model.getTags();
        model.setId(oldModel.getId());
        modelMapper.map(certificateRepository.update(modelMapper.map(model, GiftCertificateModel.class))
                                             .orElseThrow(() -> new EntityNotFoundException(
                                                     GiftCertificateDto.class, ID.getColumnName(), model.getId())
                                             ), GiftCertificateDto.class
        );
        if (tags != null) {
            linker.unlink(model);
            model.setTags(createTagsIfNotExist(tags));
            linker.link(model);
        }
        return oldModel;
    }

}
