package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.CertificateModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@SpringBootTest(classes = { TestConfig.class })
class OrderRepositoryImplTest {

    private final OrderRepository orderRepository;

    private final CertificateRepository certificateRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final Pageable pageable;

    @Autowired
    public OrderRepositoryImplTest(OrderRepository orderRepository,
                                   CertificateRepository certificateRepository,
                                   TagRepository tagRepository,
                                   UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.pageable = PageRequest.of(0, 10);
    }

    @Test
    void create_shouldCreatePurchase_always() {
        final TagModel tag1 = tagRepository.save(new TagModel("tag1"));
        final TagModel tag2 = tagRepository.save(new TagModel("tag2"));
        final Set<TagModel> tags = Set.of(tag1, tag2);
        final UserModel user = userRepository.save(getUser());
        final CertificateModel certificate = certificateRepository.save(getCertificate1(tags));
        final OrderModel purchase = getPurchaseModel(user, certificate);
        user.setBalance(BigDecimal.TEN);

        OrderModel actual = orderRepository.save(purchase);

        assertTrue(actual.getId() > 0);
        assertEquals(0, actual.getCost().compareTo(BigDecimal.ONE));
        assertNotNull(actual.getPurchaseDate());
        assertNotNull(actual.getUser());
        assertNotNull(actual.getCertificate());
        assertEquals(new BigDecimal(9), actual.getUser().getBalance());
    }

    @Test
    void findByUserId_shouldReturnUserPurchases_always() {
        final TagModel tag1 = tagRepository.save(new TagModel("tag1"));
        final TagModel tag2 = tagRepository.save(new TagModel("tag2"));
        final TagModel tag3 = tagRepository.save(new TagModel("widely"));
        final TagModel tag4 = tagRepository.save(new TagModel("xmas"));
        final Set<TagModel> tags1 = Set.of(tag1, tag2);
        final Set<TagModel> tags2 = Set.of(tag3, tag4);
        final UserModel user = userRepository.save(getUser());
        final CertificateModel certificate1 = certificateRepository.save(getCertificate1(tags1));
        final CertificateModel certificate2 = certificateRepository.save(getCertificate2(tags2));
        orderRepository.save(getPurchaseModel(user, certificate1));
        orderRepository.save(getPurchaseModel(user, certificate2));

        List<OrderModel> actual = orderRepository.findByUserId(user.getId(), pageable);

        assertEquals(2, actual.size());
        assertEquals(user.getId(), actual.get(0).getUser().getId());
        assertEquals(user.getId(), actual.get(1).getUser().getId());
    }

    @Test
    void findByIds_shouldReturnPurchase_whenExists() {
        final TagModel tag1 = tagRepository.save(new TagModel("tag1"));
        final TagModel tag2 = tagRepository.save(new TagModel("tag2"));
        final Set<TagModel> tags = Set.of(tag1, tag2);
        final UserModel user = userRepository.save(getUser());
        final CertificateModel certificate = certificateRepository.save(getCertificate1(tags));
        final OrderModel purchase = getPurchaseModel(user, certificate);
        OrderModel expected = orderRepository.save(purchase);

        Optional<OrderModel> actual1 = orderRepository.findByUserIdAndId(user.getId(), purchase.getId());
        Optional<OrderModel> actual2 = orderRepository.findByUserIdAndId(-1L, purchase.getId());
        Optional<OrderModel> actual3 = orderRepository.findByUserIdAndId(user.getId(), -1L);
        Optional<OrderModel> actual4 = orderRepository.findByUserIdAndId(-1L, -1L);

        assertTrue(actual1.isPresent());
        assertEquals(expected, actual1.get());
        assertFalse(actual2.isPresent());
        assertFalse(actual3.isPresent());
        assertFalse(actual4.isPresent());
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

    private UserModel getUser() {
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

}