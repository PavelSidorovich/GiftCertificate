package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.WiredEntityDeletionException;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.TagModel_;
import com.epam.esm.gcs.repository.TagRepository;
import com.epam.esm.gcs.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
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
        if (existsWithName(tagName)) {
            throw new DuplicatePropertyException(
                    TagDto.class, TagModel_.NAME, tagName
            );
        }
        TagModel tag = tagRepository.save(modelMapper.map(model, TagModel.class));
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
                        TagDto.class, TagModel_.ID, id
                )
        );
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
        TagModel tag = tagRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException(
                        GiftCertificateDto.class, TagModel_.NAME, name
                )
        );
        return modelMapper.map(tag, TagDto.class);
    }

    /**
     * Finds all tags
     *
     * @return list of tags
     */
    @Override
    public List<TagDto> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable).getContent().stream()
                            .map(model -> modelMapper.map(model, TagDto.class))
                            .collect(Collectors.toList());
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
        try {
            tagRepository.deleteById(id);
            tagRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new WiredEntityDeletionException(TagDto.class, TagModel_.ID, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(TagDto.class, TagModel_.ID, id);
        }
    }

    /**
     * Checks if tag with specified name exists
     *
     * @param name name of tag check for existence
     * @return true if exists, otherwise - false
     */
    @Override
    public boolean existsWithName(String name) {
        return tagRepository.existsByNameIgnoreCase(name);
    }

}
