package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class OrderRepositoryImplTest {

    private final OrderRepositoryImpl purchaseRepository;

    private final QueryLimiter queryLimiter;

    @Autowired
    public OrderRepositoryImplTest(OrderRepositoryImpl purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
        this.queryLimiter = new QueryLimiter(10, 0);
    }

    @Test
    void create_shouldCreatePurchase_always() {
        final LocalDateTime time = LocalDateTime.now();
        final OrderModel purchase = getPurchaseModel1(time);

        OrderModel actual = purchaseRepository.create(purchase);

        assertTrue(actual.getId() > 0);
        assertEquals(0, actual.getCost().compareTo(BigDecimal.ONE));
        assertEquals(-1, time.compareTo(actual.getPurchaseDate()));
        assertNotNull(actual.getUser());
        assertNotNull(actual.getCertificate());
        assertEquals(new BigDecimal(9), actual.getUser().getBalance());
    }

    @Test
    void findByUserId_shouldReturnUserPurchases_always() {
        final LocalDateTime time = LocalDateTime.now();
        final OrderModel orderModel = purchaseRepository.create(getPurchaseModel1(time));
        final UserModel user = orderModel.getUser();

        List<OrderModel> actual = purchaseRepository.findByUserId(user.getId(), queryLimiter);

        assertEquals(1, actual.size());
        assertEquals(user.getId(), actual.get(0).getUser().getId());
    }

    @Test
    void findByIds_shouldReturnPurchase_whenExists() {
        final LocalDateTime time = LocalDateTime.now();
        final OrderModel purchase = getPurchaseModel1(time);
        final OrderModel actual = purchaseRepository.create(purchase);
        final UserModel user = actual.getUser();

        Optional<OrderModel> actual1 = purchaseRepository.findByIds(user.getId(), purchase.getId());
        Optional<OrderModel> actual2 = purchaseRepository.findByIds(-1L, purchase.getId());
        Optional<OrderModel> actual3 = purchaseRepository.findByIds(user.getId(), -1L);
        Optional<OrderModel> actual4 = purchaseRepository.findByIds(-1L, -1L);

        assertTrue(actual1.isPresent());
        assertFalse(actual2.isPresent());
        assertFalse(actual3.isPresent());
        assertFalse(actual4.isPresent());
    }

    @Test
    void findTheMostActiveUser_shouldReturnUserWithTheHighestExpenses_always() {
        final LocalDateTime time = LocalDateTime.now();
        final OrderModel purchase1 = purchaseRepository.create(getPurchaseModel1(time));
        final OrderModel purchase2 = purchaseRepository.create(getPurchaseModel2(time));
        final UserModel user1 = purchase1.getUser();
        final UserModel user2 = purchase2.getUser();
        OrderModel purchase3 = new OrderModel();
        purchase3.setUser(user1);
        purchase3.setCertificate(getCertificate3(time));
        purchaseRepository.create(purchase3);

        Optional<UserModel> actual = purchaseRepository.findTheMostActiveUser();
        assertTrue(actual.isPresent());
        assertEquals(user2.getId(), actual.get().getId());
    }

    @Test
    void delete_shouldThrowUnsupportedOperationException_always() {
        assertThrows(UnsupportedOperationException.class, () -> purchaseRepository.delete(1L));
    }

    private OrderModel getPurchaseModel1(LocalDateTime time) {
        final GiftCertificateModel certificate = getCertificate1(time);
        final UserModel user = new UserModel(10L, "fname", "lname", "email", BigDecimal.TEN);
        final OrderModel purchase = new OrderModel();

        purchase.setUser(user);
        purchase.setCertificate(certificate);
        purchase.setPurchaseDate(time);

        return purchase;
    }

    private OrderModel getPurchaseModel2(LocalDateTime time) {
        final GiftCertificateModel certificate = getCertificate2(time);
        final UserModel user = new UserModel(20L, "newName", "newSurname", "email@", BigDecimal.TEN);
        final OrderModel purchase = new OrderModel();

        purchase.setUser(user);
        purchase.setCertificate(certificate);
        purchase.setPurchaseDate(time);

        return purchase;
    }

    private GiftCertificateModel getCertificate1(LocalDateTime time) {
        return GiftCertificateModel
                .builder()
                .id(1L)
                .name("certName")
                .description("desc")
                .price(BigDecimal.ONE)
                .duration(10)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagModel("tag1"), new TagModel("tag2")))
                .build();
    }

    private GiftCertificateModel getCertificate2(LocalDateTime time) {
        return GiftCertificateModel
                .builder()
                .id(2L)
                .name("certName1")
                .description("desc")
                .price(new BigDecimal("4"))
                .duration(20)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagModel("widely"), new TagModel("xmas")))
                .build();
    }

    private GiftCertificateModel getCertificate3(LocalDateTime time) {
        return GiftCertificateModel
                .builder()
                .id(3L)
                .name("certName3")
                .description("desc")
                .price(new BigDecimal("2"))
                .duration(10)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagModel("tag4")))
                .build();
    }

}