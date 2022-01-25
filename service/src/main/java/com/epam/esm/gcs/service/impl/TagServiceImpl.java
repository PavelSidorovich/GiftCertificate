package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.repository.mapper.TagColumn;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Override
    public TagDto create(TagDto model) {
        final String tagName = model.getName();

        if (tagRepository.existsWithName(tagName)) {
            throw new DuplicatePropertyException(
                    TagDto.class, TagColumn.NAME.getColumnName(), tagName
            );
        }

        return modelMapper.map(
                tagRepository.create(modelMapper.map(model, TagModel.class)),
                TagDto.class
        );
    }

    @Override
    public TagDto findById(long id) {
        return modelMapper.map(
                tagRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(
                                TagDto.class, TagColumn.ID.getColumnName(), id
                        )
                ), TagDto.class
        );
    }

    @Override
    public TagDto findByName(String name) {
        return modelMapper.map(
                tagRepository.findByName(name).orElseThrow(
                        () -> new EntityNotFoundException(
                                GiftCertificateDto.class,
                                TagColumn.NAME.getColumnName(),
                                name)
                ), TagDto.class
        );
    }

    @Override
    public List<TagDto> findAll() {
        return tagRepository.findAll().stream()
                            .map(model -> modelMapper.map(model, TagDto.class))
                            .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        if (!tagRepository.delete(id)) {
            throw new EntityNotFoundException(
                    TagDto.class, TagColumn.ID.getColumnName(), id
            );
        }
    }

    @Override
    public boolean existsWithName(String name) {
        return tagRepository.existsWithName(name);
    }

}
