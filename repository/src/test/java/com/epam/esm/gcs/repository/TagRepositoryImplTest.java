package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.TagModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class TagRepositoryImplTest {

    private final TagRepository tagRepository;

    @Autowired
    public TagRepositoryImplTest(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Test
    void create_shouldReturnCreatedTag_ifNameIsUnique() {
        final String expected = "testName";
        final TagModel tag = tagRepository.save(new TagModel(expected));

        assertEquals("testname", tag.getName());
        assertTrue(tag.getId().compareTo(0L) > 0);
    }

    @Test
    void create_shouldThrowException_ifNameIsNotUnique() {
        final String tagName = "testName";

        tagRepository.save(new TagModel(tagName));

        assertThrows(DataIntegrityViolationException.class, () ->
                tagRepository.save(new TagModel(tagName))
        );
    }

    @Test
    void findById_shouldReturnTagModel_ifExistsWithId() {
        final TagModel expected = tagRepository.save(new TagModel("testName"));

        Optional<TagModel> actualTag = tagRepository.findById(expected.getId());

        assertTrue(actualTag.isPresent());
        assertEquals(expected, actualTag.get());
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifNotExistsWithId() {
        Optional<TagModel> actualTag = tagRepository.findById(100000L);

        assertTrue(actualTag.isEmpty());
    }

    @Test
    void findByName_shouldReturnTagModel_ifExistsWithName() {
        final TagModel expected = tagRepository.save(new TagModel("testName"));

        Optional<TagModel> actual = tagRepository.findByNameIgnoreCase(expected.getName());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotExistsWithName() {
        Optional<TagModel> actual = tagRepository.findByNameIgnoreCase("testName");

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll_shouldReturnListOfTags_always() {
        tagRepository.save(new TagModel("testName1"));
        tagRepository.save(new TagModel("testName2"));

        final List<TagModel> actual = tagRepository
                .findAll(PageRequest.of(0, 10))
                .getContent();

        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    void findAll_shouldReturnLimitedListOfTags_whenHasLimits() {
        tagRepository.save(new TagModel("testName1"));
        tagRepository.save(new TagModel("testName2"));
        tagRepository.save(new TagModel("testName3"));

        final List<TagModel> actual = tagRepository
                .findAll(PageRequest.of(0, 1))
                .getContent();

        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @Test
    void delete_shouldDeleteTag_whenFound() {
        final TagModel tag = tagRepository.save(new TagModel("testName"));
        final Long tagId = tag.getId();

        tagRepository.deleteById(tagId);

        assertTrue(tagRepository.findById(tagId).isEmpty());
    }

    @Test
    void existsWithName_shouldReturnTrue_ifSuchRowExists() {
        tagRepository.save(new TagModel("testName"));

        assertTrue(tagRepository.existsByNameIgnoreCase("testname"));
    }

    @Test
    void existsWithName_shouldReturnFalse_ifSuchRowNotExists() {
        assertFalse(tagRepository.existsByNameIgnoreCase("testName"));
    }

}