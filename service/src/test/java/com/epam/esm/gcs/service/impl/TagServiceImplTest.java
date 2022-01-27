package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.config.ModelMapperConfig;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    private final TagServiceImpl tagService;

    private final TagRepository tagRepository;

    public TagServiceImplTest(@Mock TagRepository tagRepository) {
        tagService = new TagServiceImpl(
                tagRepository, new ModelMapperConfig().modelMapper()
        );
        this.tagRepository = tagRepository;
    }

    @Test
    void create_shouldCreateDto_ifUniqueTagName() {
        final String tagName = "testName";
        final long tagId = 1L;
        final TagDto tagDtoToCreate = new TagDto(null, tagName);
        final TagDto tagDtoToReturn = new TagDto(tagId, tagName);
        final TagModel tagModelToCreate = new TagModel(tagName);
        final TagModel tagModelToReturn = new TagModel(tagId, tagName);

        when(tagRepository.existsWithName(tagName)).thenReturn(false);
        when(tagRepository.create(tagModelToCreate)).thenReturn(tagModelToReturn);

        assertEquals(tagDtoToReturn, tagService.create(tagDtoToCreate));
        verify(tagRepository).existsWithName(tagName);
        verify(tagRepository).create(tagModelToCreate);
    }

    @Test
    void create_shouldThrowDuplicatePropertyException_ifTagNameIsNotUnique() {
        final String tagName = "testName";
        final long tagId = 1L;
        final TagDto tagDtoToCreate = new TagDto(null, tagName);
        final TagModel tagModelToCreate = new TagModel(tagName);
        final TagModel tagModelToReturn = new TagModel(tagId, tagName);

        when(tagRepository.existsWithName(tagName)).thenReturn(true);
        when(tagRepository.create(tagModelToCreate)).thenReturn(tagModelToReturn);

        assertThrows(DuplicatePropertyException.class, () -> tagService.create(tagDtoToCreate));
        verify(tagRepository).existsWithName(tagName);
        verify(tagRepository, times(0)).create(tagModelToCreate);
    }

    @Test
    void findById_shouldReturnTagDto_ifExistsWithId() {
        final long tagId = 1L;
        final String tagName = "testName";
        final TagModel tagModel = new TagModel(tagId, tagName);
        final TagDto expected = new TagDto(tagId, tagName);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tagModel));

        assertEquals(expected, tagService.findById(tagId));
        verify(tagRepository).findById(tagId);
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_ifCanNotFindTagWithId() {
        final long tagId = 1L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.findById(tagId));
        verify(tagRepository).findById(tagId);
    }

    @Test
    void findByName_shouldReturnTagDto_ifExistsWithName() {
        final long tagId = 1L;
        final String tagName = "testName";
        final TagModel tagModel = new TagModel(tagId, tagName);
        final TagDto expected = new TagDto(tagId, tagName);

        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tagModel));

        assertEquals(expected, tagService.findByName(tagName));
        verify(tagRepository).findByName(tagName);
    }

    @Test
    void findByName_shouldThrowEntityNotFoundException_ifCanNotFindTagWithName() {
        final String tagName = "testName";

        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.findByName(tagName));
        verify(tagRepository).findByName(tagName);
    }

    @Test
    void findAll_shouldReturnListOfTags_always() {
        final List<TagModel> tagModels = List.of(
                new TagModel(1L, "Car"),
                new TagModel(2L, "Game"),
                new TagModel(5L, "Courses")
        );
        when(tagRepository.findAll()).thenReturn(tagModels);

        final List<TagDto> dtoList = tagService.findAll();

        assertEquals(3, dtoList.size());
        assertTrue(dtoList.containsAll(
                List.of(
                        new TagDto(1L, "Car"),
                        new TagDto(2L, "Game"),
                        new TagDto(5L, "Courses")
                )
        ));
        verify(tagRepository).findAll();
    }

    @Test
    void delete_shouldDeleteEntityById_ifWasFoundAndDeleted() {
        final long tagId = 1L;
        when(tagRepository.delete(tagId)).thenReturn(true);

        tagService.delete(tagId);

        verify(tagRepository).delete(tagId);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_ifEntityNotExists() {
        final long tagId = 1L;
        when(tagRepository.delete(tagId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> tagService.delete(tagId));
        verify(tagRepository).delete(tagId);
    }

    @Test
    void existsWithName_shouldReturnTrue_ifTagExistsWithName() {
        final String testName = "testName";
        when(tagRepository.existsWithName(testName)).thenReturn(true);

        assertTrue(tagService.existsWithName(testName));
    }

}