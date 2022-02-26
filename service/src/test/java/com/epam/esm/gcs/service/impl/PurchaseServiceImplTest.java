package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedGiftCertificateDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.PurchaseRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    private final PurchaseServiceImpl purchaseService;

    private final PurchaseRepository purchaseRepository;
    private final UserService userService;
    private final GiftCertificateService certificateService;
    private final ModelMapper mapper;

    public PurchaseServiceImplTest(@Mock PurchaseRepository purchaseRepository,
                                   @Mock UserService userService,
                                   @Mock GiftCertificateService certificateService) {
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
        this.certificateService = certificateService;
        this.mapper = new ModelMapper();
        this.purchaseService = new PurchaseServiceImpl(
                userService, purchaseRepository,
                certificateService, mapper);
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenUserNotFound() {
        final long userId = 1L;
        final TruncatedGiftCertificateDto giftCertificateDto = new TruncatedGiftCertificateDto("certName");
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                     () -> purchaseService.purchase(userId, giftCertificateDto));
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenCertificateNotFound() {
        final UserDto user = getUser();
        final long userId = user.getId();
        final TruncatedGiftCertificateDto giftCertificateDto =
                new TruncatedGiftCertificateDto("certName");
        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findByName("certName")).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                     () -> purchaseService.purchase(userId, giftCertificateDto));
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenUserHasGotNotEnoughMoneyForPurchase() {
        final UserDto user = getUser();
        final long userId = user.getId();
        final TruncatedGiftCertificateDto giftCertificateDto =
                new TruncatedGiftCertificateDto("certName");
        final GiftCertificateDto certificate = getCertificate1(LocalDateTime.now());
        user.setBalance(BigDecimal.ZERO);

        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findByName("certName")).thenReturn(certificate);

        assertThrows(NotEnoughMoneyException.class,
                     () -> purchaseService.purchase(userId, giftCertificateDto));
    }

    @Test
    void purchase_shouldReturnSavedPurchase_whenCertificateAndUserExists() {
        final LocalDateTime time = LocalDateTime.now();
        final UserDto user = getUser();
        final long userId = user.getId();
        final TruncatedGiftCertificateDto giftCertificateDto =
                new TruncatedGiftCertificateDto("certName");
        final OrderDto purchase = getPurchaseDto();
        final OrderModel beforePurchase = mapper.map(purchase, OrderModel.class);
        final OrderDto expected = mapper.map(getPurchaseModel(time), OrderDto.class);
        final GiftCertificateDto certificate = getCertificate1(time);
        beforePurchase.setUser(mapper.map(user, UserModel.class));
        beforePurchase.setCertificate(mapper.map(certificate, GiftCertificateModel.class));
        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findByName("certName")).thenReturn(certificate);
        when(purchaseRepository.create(beforePurchase)).thenReturn(getPurchaseModel(time));

        OrderDto actual = purchaseService.purchase(userId, giftCertificateDto);

        assertEquals(expected, actual);
        verify(purchaseRepository).flushAndClear();
    }

    @Test
    void findByUserId_shouldReturnUserPurchases_whenExists() {
        final QueryLimiter limiter = new QueryLimiter(10, 0);
        final UserDto user = getUser();
        final long userId = user.getId();
        final OrderDto purchaseDto = getPurchaseDto();
        final OrderModel orderModel = mapper.map(purchaseDto, OrderModel.class);
        final List<OrderDto> expected = List.of(purchaseDto);

        when(userService.findById(userId)).thenReturn(user);
        when(purchaseRepository.findByUserId(userId, limiter)).thenReturn(List.of(orderModel));

        List<OrderDto> actual = purchaseService.findByUserId(userId, limiter);

        assertEquals(expected, actual);
        verify(purchaseRepository).clear();
    }

    @Test
    void findByUserId_shouldThrowEntityNotFoundException_whenUserNotExists() {
        final QueryLimiter limiter = new QueryLimiter(10, 0);
        final long userId = 1L;
        final OrderDto purchaseDto = getPurchaseDto();
        final OrderModel orderModel = mapper.map(purchaseDto, OrderModel.class);

        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);
        when(purchaseRepository.findByUserId(userId, limiter)).thenReturn(List.of(orderModel));

        assertThrows(EntityNotFoundException.class,
                     () -> purchaseService.findByUserId(userId, limiter));
    }

    @Test
    void findTruncatedByIds_shouldReturnTruncatedPurchase_whenUserAndPurchaseExists() {
        final UserDto user = getUser();
        final long purchaseId = 1L;
        final long userId = user.getId();
        final OrderModel orderModel = getPurchaseModel(LocalDateTime.now());
        final TruncatedOrderDto expected =
                new TruncatedOrderDto(purchaseId, orderModel.getCost(), orderModel.getPurchaseDate());

        when(userService.findById(userId)).thenReturn(user);
        when(purchaseRepository.findByIds(userId, purchaseId)).thenReturn(Optional.of(orderModel));

        TruncatedOrderDto truncated = purchaseService.findTruncatedByIds(userId, purchaseId);

        assertEquals(expected, truncated);
        verify(purchaseRepository).clear();
    }

    @Test
    void findTruncatedByIds_shouldThrowEntityNotFoundException_whenUserNotExists() {
        final long userId = 1L;
        final long purchaseId = 1L;
        final OrderModel orderModel = getPurchaseModel(LocalDateTime.now());

        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);
        when(purchaseRepository.findByIds(userId, purchaseId)).thenReturn(Optional.of(orderModel));

        assertThrows(EntityNotFoundException.class,
                     () -> purchaseService.findTruncatedByIds(userId, purchaseId));
        verify(purchaseRepository, times(0)).clear();
    }

    @Test
    void findTruncatedByIds_shouldThrowEntityNotFoundException_whenPurchaseNotExists() {
        final UserDto user = getUser();
        final long purchaseId = 1L;
        final long userId = user.getId();

        when(userService.findById(userId)).thenReturn(user);
        when(purchaseRepository.findByIds(userId, purchaseId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                     () -> purchaseService.findTruncatedByIds(userId, purchaseId));
        verify(purchaseRepository, times(0)).clear();
    }

    @Test
    void findMostWidelyTag_shouldReturnTheMoseWidelyUsedTag_whenTheMostActiveUserExists() {
        final UserModel user = mapper.map(getUser(), UserModel.class);
        final QueryLimiter limiter = new QueryLimiter(Integer.MAX_VALUE, 0);
        final TagDto expected = new TagDto(1L, "tag1");

        when(purchaseRepository.findTheMostActiveUser()).thenReturn(Optional.of(user));
        when(purchaseRepository.findByUserId(user.getId(), limiter)).thenReturn(getUserPurchases(user));

        TagDto actual = purchaseService.findMostWidelyTag();

        assertEquals(expected, actual);
        verify(purchaseRepository).clear();
    }

    @Test
    void findMostWidelyTag_shouldThrowNoWidelyUsedTagException_whenTheMostActiveUserNotExists() {
        final UserModel user = mapper.map(getUser(), UserModel.class);
        final QueryLimiter limiter = new QueryLimiter(Integer.MAX_VALUE, 0);

        when(purchaseRepository.findTheMostActiveUser()).thenReturn(Optional.empty());
        when(purchaseRepository.findByUserId(user.getId(), limiter)).thenReturn(getUserPurchases(user));

        assertThrows(NoWidelyUsedTagException.class, purchaseService::findMostWidelyTag);
        verify(purchaseRepository, times(0)).clear();
    }

    @Test
    void findMostWidelyTag_shouldThrowNoWidelyUsedTagException_whenThereIsNoWidelyTag() {
        final UserModel user = mapper.map(getUser(), UserModel.class);
        final QueryLimiter limiter = new QueryLimiter(Integer.MAX_VALUE, 0);

        when(purchaseRepository.findTheMostActiveUser()).thenReturn(Optional.of(user));
        when(purchaseRepository.findByUserId(user.getId(), limiter)).thenReturn(Collections.emptyList());

        assertThrows(NoWidelyUsedTagException.class, purchaseService::findMostWidelyTag);
        verify(purchaseRepository, times(0)).clear();
    }

    private OrderDto getPurchaseDto() {
        return new OrderDto(GiftCertificateDto.builder().name("certName").build(), null);
    }

    private OrderModel getPurchaseModel(LocalDateTime time) {
        return new OrderModel(
                1L,
                mapper.map(getCertificate1(time), GiftCertificateModel.class),
                mapper.map(getUser(), UserModel.class),
                BigDecimal.ONE,
                time
        );
    }

    private UserDto getUser() {
        return new UserDto(
                20L, "newName", "newSurname",
                "email@", BigDecimal.TEN
        );
    }

    private GiftCertificateDto getCertificate1(LocalDateTime time) {
        return GiftCertificateDto
                .builder()
                .id(1L)
                .name("certName")
                .description("desc")
                .price(BigDecimal.ONE)
                .duration(10)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagDto(1L, "tag1"), new TagDto(2L, "tag2")))
                .build();
    }

    private GiftCertificateDto getCertificate2(LocalDateTime time) {
        return GiftCertificateDto
                .builder()
                .id(2L)
                .name("certName")
                .description("desc")
                .price(BigDecimal.ONE)
                .duration(10)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagDto(1L, "tag1"), new TagDto(2L, "tag3")))
                .build();
    }

    private GiftCertificateDto getCertificate3(LocalDateTime time) {
        return GiftCertificateDto
                .builder()
                .id(3L)
                .name("certName")
                .description("desc")
                .price(BigDecimal.ONE)
                .duration(10)
                .createDate(time)
                .lastUpdateDate(time)
                .tags(Set.of(new TagDto(1L, "tag1"), new TagDto(2L, "tag4")))
                .build();
    }

    private List<OrderModel> getUserPurchases(UserModel user) {
        LocalDateTime time = LocalDateTime.now();
        return List.of(
                new OrderModel(1L, mapper.map(getCertificate1(time), GiftCertificateModel.class),
                               user, BigDecimal.TEN, time),
                new OrderModel(2L, mapper.map(getCertificate2(time), GiftCertificateModel.class),
                               user, BigDecimal.TEN, time),
                new OrderModel(3L, mapper.map(getCertificate3(time), GiftCertificateModel.class),
                               user, BigDecimal.TEN, time)
        );
    }

}