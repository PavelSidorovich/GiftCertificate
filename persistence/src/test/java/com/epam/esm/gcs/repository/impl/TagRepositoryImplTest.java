package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
        final String tagName = "testName";
        final TagModel tag = tagRepository.create(new TagModel(tagName));

        assertEquals(tagName, tag.getName());
        assertTrue(tag.getId().compareTo(0L) > 0);
    }

    @Test
    void create_shouldThrowException_ifNameIsNotUnique() {
        final String tagName = "testName";

        tagRepository.create(new TagModel(tagName));
        assertThrows(DuplicateKeyException.class, () ->
                tagRepository.create(new TagModel(tagName))
        );
    }

    @Test
    void findById_shouldReturnTagModel_ifExistsWithId() {
        final TagModel tag = tagRepository.create(new TagModel("testName"));

        Optional<TagModel> actualTag = tagRepository.findById(tag.getId());

        assertTrue(actualTag.isPresent());
        assertEquals(tag, actualTag.get());
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifNotExistsWithId() {
        Optional<TagModel> actualTag = tagRepository.findById(100000L);

        assertTrue(actualTag.isEmpty());
    }

    @Test
    void findByName_shouldReturnTagModel_ifExistsWithName() {
        final TagModel tag = tagRepository.create(new TagModel("testName"));

        Optional<TagModel> actualTag = tagRepository.findByName(tag.getName());

        assertTrue(actualTag.isPresent());
        assertEquals(tag, actualTag.get());
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotExistsWithName() {
        Optional<TagModel> actualTag = tagRepository.findByName("testName");

        assertTrue(actualTag.isEmpty());
    }

    @Test
    void findAll_shouldReturnListOfTags_always() {
        final List<TagModel> tags = tagRepository.findAll();

        assertNotNull(tags);
        assertEquals(10, tags.size());
    }

    @Test
    void delete_shouldReturnTrue_whenIsDeleted() {
        final TagModel tag = tagRepository.create(new TagModel("testName"));
        final Long tagId = tag.getId();

        assertTrue(tagRepository.delete(tagId));
        assertTrue(tagRepository.findById(tagId).isEmpty());
    }

    @Test
    void delete_shouldReturnFalse_whenSuchRowNotExists() {
        assertFalse(tagRepository.delete(100000L));
    }

    @Test
    void existsWithName_shouldReturnTrue_ifSuchRowExists() {
        tagRepository.create(new TagModel("testName"));

        assertTrue(tagRepository.existsWithName("testName"));
    }

    @Test
    void existsWithName_shouldReturnFalse_ifSuchRowNotExists() {
        assertFalse(tagRepository.existsWithName("testName"));
    }

}