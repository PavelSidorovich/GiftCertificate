package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.CertificateModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class TagRepositoryImplTest {

    private final TagRepository tagRepository;

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public TagRepositoryImplTest(TagRepository tagRepository,
                                 CertificateRepository certificateRepository,
                                 UserRepository userRepository,
                                 OrderRepository orderRepository) {
        this.tagRepository = tagRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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

    @Test
    void findMostUsedTag_shouldReturnTagOfBestUser_whenOrdersExist() {
        final TagModel tag1 = tagRepository.save(new TagModel("tag1"));
        final TagModel tag2 = tagRepository.save(new TagModel("tag2"));
        final TagModel tag3 = tagRepository.save(new TagModel("widely"));
        final TagModel tag4 = tagRepository.save(new TagModel("xmas"));
        final TagModel tag5 = tagRepository.save(new TagModel("tag4"));
        final Set<TagModel> tags1 = Set.of(tag1, tag2, tag3);
        final Set<TagModel> tags2 = Set.of(tag3, tag4);
        final Set<TagModel> tags3 = Set.of(tag5, tag2);
        final UserModel user1 = userRepository.save(getUser1());
        final UserModel user2 = userRepository.save(getUser2());
        final CertificateModel certificate1 = certificateRepository.save(getCertificate1(tags1));
        final CertificateModel certificate2 = certificateRepository.save(getCertificate2(tags2));
        final CertificateModel certificate3 = certificateRepository.save(getCertificate3(tags3));
        orderRepository.save(getPurchaseModel(user1, certificate1));
        orderRepository.save(getPurchaseModel(user1, certificate2));
        orderRepository.save(getPurchaseModel(user2, certificate3));

        Optional<TagModel> actual = tagRepository.findMostUsedTag();

        assertTrue(actual.isPresent());
        assertEquals("widely", actual.get().getName());
    }

    @Test
    void findMostUsedTag_shouldReturnOptionalEmpty_whenNotFound() {
        final Optional<TagModel> actual = tagRepository.findMostUsedTag();

        assertFalse(actual.isPresent());
    }

    private OrderModel getPurchaseModel(UserModel user, CertificateModel certificateModel) {
        final OrderModel purchase = new OrderModel();

        purchase.setUser(user);
        purchase.setCertificate(certificateModel);

        return purchase;
    }

    private CertificateModel getCertificate1(Set<TagModel> tags) {
        return CertificateModel
                .builder()
                .name("certName")
                .description("desc")
                .price(BigDecimal.ONE)
                .duration(10)
                .tags(tags)
                .build();
    }

    private CertificateModel getCertificate2(Set<TagModel> tags) {
        return CertificateModel
                .builder()
                .name("certName1")
                .description("desc")
                .price(new BigDecimal("4"))
                .duration(20)
                .tags(tags)
                .build();
    }

    private CertificateModel getCertificate3(Set<TagModel> tags) {
        return CertificateModel
                .builder()
                .name("certName3")
                .description("desc")
                .price(new BigDecimal("2"))
                .duration(10)
                .tags(tags)
                .build();
    }

    private UserModel getUser1() {
        return UserModel.builder()
                        .email("email@gmail.com")
                        .password("pass")
                        .enabled(true)
                        .firstName("newName")
                        .lastName("newSurname")
                        .balance(BigDecimal.TEN)
                        .roles(Collections.emptySet())
                        .build();
    }

    private UserModel getUser2() {
        return UserModel.builder()
                        .email("email@mail.ru")
                        .password("password")
                        .enabled(true)
                        .firstName("testName")
                        .lastName("testSurname")
                        .balance(BigDecimal.TEN)
                        .roles(Collections.emptySet())
                        .build();
    }

}