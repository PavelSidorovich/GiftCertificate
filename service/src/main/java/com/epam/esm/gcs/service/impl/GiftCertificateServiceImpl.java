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

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.gcs.mapper.GiftCertificateColumn.*;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private final TagService tagService;
    private final GiftCertificateTagDtoRepository linker;
    private final GiftCertificateRepository certificateRepository;
    private final ModelMapper modelMapper;

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
        List<TagDto> tagsToCreate = modelTags.stream()
                                             .filter(tag -> !tagService.existsWithName(tag.getName()))
                                             .collect(Collectors.toList());
        modelTags.removeAll(tagsToCreate);
        List<TagDto> createdTags = tagsToCreate.stream()
                                               .peek(tag -> tag.setId(tagService.create(tag).getId()))
                                               .collect(Collectors.toList());
        List<TagDto> foundTags = modelTags.stream()
                                          .peek(tag -> tag.setId(tagService.findByName(tag.getName()).getId()))
                                          .collect(Collectors.toList());
        createdTags.addAll(foundTags);
        return createdTags;
    }

    @Override
    public GiftCertificateDto findById(long id) {
        return linker.findById(id);
    }

    @Override
    public GiftCertificateDto findByName(String name) {
        return linker.findByName(name);
    }

    @Override
    public List<GiftCertificateDto> findAll() {
        return linker.findAll();
    }

    @Override
    public void delete(long id) {
        if (!certificateRepository.delete(id)) {
            throw new EntityNotFoundException(GiftCertificateDto.class, ID.getColumnName(), id);
        }
    }

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
