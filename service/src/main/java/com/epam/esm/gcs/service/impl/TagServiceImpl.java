package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.mapper.TagColumn;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.impl.TagRepository;
import com.epam.esm.gcs.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;
    private ModelMapper modelMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, ModelMapper modelMapper) {
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TagDto create(TagDto model) {
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
                                TagColumn.getModelName(), TagColumn.ID.getColumnName(), id
                        )
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
                    TagColumn.getModelName(), TagColumn.ID.getColumnName(), id
            );
        }
    }

}
