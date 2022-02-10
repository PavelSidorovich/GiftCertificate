package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.repository.column.TagColumn;
import com.epam.esm.gcs.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates new tag
     *
     * @param model tag to create
     * @return created tag with generated id
     * @throws DuplicatePropertyException if tag with such name already exists
     */
    @Override
    @Transactional
    public TagDto create(TagDto model) {
        final String tagName = model.getName();
        if (tagRepository.existsWithName(tagName)) {
            throw new DuplicatePropertyException(
                    TagDto.class, TagColumn.NAME.getColumnName(), tagName
            );
        }
        TagModel tag = tagRepository.create(modelMapper.map(model, TagModel.class));
        tagRepository.flushAndClear();
        return modelMapper.map(tag, TagDto.class);
    }

    /**
     * Finds tag with provided id
     *
     * @param id id of tag to find
     * @return tag if found
     * @throws EntityNotFoundException if tag with provided id not found
     */
    @Override
    public TagDto findById(long id) {
        TagModel tag = tagRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        TagDto.class, TagColumn.ID.getColumnName(), id
                )
        );
        tagRepository.clear();
        return modelMapper.map(tag, TagDto.class);
    }

    /**
     * Finds tag with provided name
     *
     * @param name name of tag to find
     * @return tag if found
     * @throws EntityNotFoundException if tag with provided name not found
     */
    @Override
    public TagDto findByName(String name) {
        TagModel tag = tagRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, TagColumn.NAME.getColumnName(), name
                )
        );
        tagRepository.clear();
        return modelMapper.map(tag, TagDto.class);
    }

    /**
     * Finds all tags
     *
     * @return list of tags
     */
    @Override
    public List<TagDto> findAll() {
        List<TagDto> tags = tagRepository.findAll().stream()
                                         .map(model -> modelMapper.map(model, TagDto.class))
                                         .collect(Collectors.toList());
        tagRepository.clear();
        return tags;
    }

    /**
     * Deletes tag with specified id
     *
     * @param id id of tag to delete
     * @throws EntityNotFoundException if tag with provided id not found
     */
    @Override
    @Transactional
    public void delete(long id) {
        if (!tagRepository.delete(id)) {
            throw new EntityNotFoundException(
                    TagDto.class, TagColumn.ID.getColumnName(), id
            );
        }
        tagRepository.flushAndClear();
    }

    /**
     * Checks if tag with specified name exists
     *
     * @param name name of tag check for existence
     * @return true if exists, otherwise - false
     */
    @Override
    public boolean existsWithName(String name) {
        boolean exists = tagRepository.existsWithName(name);
        tagRepository.clear();
        return exists;
    }

}
