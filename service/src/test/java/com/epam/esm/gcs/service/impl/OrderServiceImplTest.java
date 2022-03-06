package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.OrderRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private final OrderServiceImpl orderService;

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final GiftCertificateService certificateService;
    private final ModelMapper mapper;
    private final Pageable pageable;

    public OrderServiceImplTest(@Mock OrderRepository orderRepository,
                                @Mock UserService userService,
                                @Mock GiftCertificateService certificateService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.certificateService = certificateService;
        this.mapper = new ModelMapper();
        this.orderService = new OrderServiceImpl(
                userService, orderRepository,
                certificateService, mapper);
        this.pageable = PageRequest.of(0, 10);
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenUserNotFound() {
        final long userId = 1L;
        final long certificateId = 1L;
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                     () -> orderService.purchase(userId, certificateId));
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenCertificateNotFound() {
        final UserDto user = getUser();
        final long userId = user.getId();
        final long certificateId = 1L;
        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findById(certificateId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                     () -> orderService.purchase(userId, certificateId));
    }

    @Test
    void purchase_shouldThrowEntityNotFoundException_whenUserHasGotNotEnoughMoneyForPurchase() {
        final UserDto user = getUser();
        final long userId = user.getId();
        final long certificateId = 1L;
        final GiftCertificateDto certificate = getCertificate1(LocalDateTime.now());
        user.setBalance(BigDecimal.ZERO);

        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findById(certificateId)).thenReturn(certificate);

        assertThrows(NotEnoughMoneyException.class,
                     () -> orderService.purchase(userId, certificateId));
    }

    @Test
    void purchase_shouldReturnSavedOrder_whenCertificateAndUserExists() {
        final LocalDateTime time = LocalDateTime.now();
        final UserDto user = getUser();
        final long userId = user.getId();
        final long certificateId = 1L;
        final OrderDto orderDto = getOrderDto();
        final OrderModel beforePurchase = mapper.map(orderDto, OrderModel.class);
        final OrderDto expected = mapper.map(getOrderModel(time), OrderDto.class);
        final GiftCertificateDto certificate = getCertificate1(time);
        certificate.setTags(new HashSet<>());
        final GiftCertificateModel certificateModel = mapper.map(certificate, GiftCertificateModel.class);
        beforePurchase.setUser(mapper.map(user, UserModel.class));
        beforePurchase.setCertificate(certificateModel);
        when(userService.findById(userId)).thenReturn(user);
        when(certificateService.findById(certificateId)).thenReturn(certificate);
        when(orderRepository.save(beforePurchase)).thenReturn(getOrderModel(time));

        OrderDto actual = orderService.purchase(userId, certificateId);

        assertEquals(expected, actual);
    }

    @Test
    void findByUserId_shouldReturnUserOrders_whenExists() {
        final UserDto user = getUser();
        final long userId = user.getId();
        final OrderDto orderDto = getOrderDto();
        final OrderModel orderModel = mapper.map(orderDto, OrderModel.class);
        final List<OrderDto> expected = List.of(orderDto);

        when(userService.findById(userId)).thenReturn(user);
        when(orderRepository.findByUserId(userId, pageable)).thenReturn(List.of(orderModel));

        List<OrderDto> actual = orderService.findByUserId(userId, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void findByUserId_shouldThrowEntityNotFoundException_whenUserNotExists() {
        final long userId = 1L;
        final OrderDto orderDto = getOrderDto();
        final OrderModel orderModel = mapper.map(orderDto, OrderModel.class);

        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);
        when(orderRepository.findByUserId(userId, pageable)).thenReturn(List.of(orderModel));

        assertThrows(EntityNotFoundException.class,
                     () -> orderService.findByUserId(userId, pageable));
    }

    @Test
    void findTruncatedByIds_shouldReturnTruncatedOrder_whenUserAndOrderExists() {
        final UserDto user = getUser();
        final long orderId = 1L;
        final long userId = user.getId();
        final OrderModel orderModel = getOrderModel(LocalDateTime.now());
        final TruncatedOrderDto expected =
                new TruncatedOrderDto(orderId, orderModel.getCost(), orderModel.getPurchaseDate());

        when(userService.findById(userId)).thenReturn(user);
        when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(Optional.of(orderModel));

        TruncatedOrderDto truncated = orderService.findTruncatedByIds(userId, orderId);

        assertEquals(expected, truncated);
    }

    @Test
    void findTruncatedByIds_shouldThrowEntityNotFoundException_whenUserNotExists() {
        final long userId = 1L;
        final long orderId = 1L;
        final OrderModel orderModel = getOrderModel(LocalDateTime.now());

        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);
        when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(Optional.of(orderModel));

        assertThrows(EntityNotFoundException.class,
                     () -> orderService.findTruncatedByIds(userId, orderId));
    }

    @Test
    void findTruncatedByIds_shouldThrowEntityNotFoundException_whenOrderNotExists() {
        final UserDto user = getUser();
        final long orderId = 1L;
        final long userId = user.getId();

        when(userService.findById(userId)).thenReturn(user);
        when(orderRepository.findByUserIdAndId(userId, orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                     () -> orderService.findTruncatedByIds(userId, orderId));
    }

    // TODO: 2/27/2022 remove
//    @Test
//    void findMostWidelyTag_shouldReturnTheMoseWidelyUsedTag_whenTheMostActiveUserExists() {
//        final UserDto user = getUser();
//        final TagDto expected = new TagDto(1L, "tag1");
//
//        when(userService.findTheMostActiveUser()).thenReturn(user);
//        when(orderRepository.findByUserId(user.getId(), Pageable.unpaged())).thenReturn(getUserOrders(user));
//
//        TagDto actual = orderService.findMostWidelyTag();
//
//        assertEquals(expected, actual);
//    }

//    @Test
//    void findMostWidelyTag_shouldThrowNoWidelyUsedTagException_whenTheMostActiveUserNotExists() {
//        final UserDto user = getUser();
//
//        when(userService.findTheMostActiveUser()).thenThrow(NoWidelyUsedTagException.class);
//        when(orderRepository.findByUserId(user.getId(), Pageable.unpaged())).thenReturn(getUserOrders(user));
//
//        assertThrows(NoWidelyUsedTagException.class, orderService::findMostWidelyTag);
//    }

//    @Test
//    void findMostWidelyTag_shouldThrowNoWidelyUsedTagException_whenThereIsNoWidelyTag() {
//        final UserDto user = getUser();
//
//        when(userService.findTheMostActiveUser()).thenReturn(user);
//        when(orderRepository.findByUserId(user.getId(), Pageable.unpaged())).thenReturn(Collections.emptyList());
//
//        assertThrows(NoWidelyUsedTagException.class, orderService::findMostWidelyTag);
//    }

    private OrderDto getOrderDto() {
        return new OrderDto(GiftCertificateDto.builder().name("certName").build(), null);
    }

    private OrderModel getOrderModel(LocalDateTime time) {
        return new OrderModel(
                1L,
                mapper.map(getCertificate1(time), GiftCertificateModel.class),
                mapper.map(getUser(), UserModel.class),
                BigDecimal.ONE,
                time
        );
    }

    private UserDto getUser() {
        return UserDto.userDtoBuilder()
                      .id(20L)
                      .email("email@")
                      .password("pass")
                      .firstName("newName")
                      .lastName("newSurname")
                      .balance(BigDecimal.TEN)
                      .build();
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

    private List<OrderModel> getUserOrders(UserDto userDto) {
        final UserModel user = mapper.map(userDto, UserModel.class);
        final LocalDateTime time = LocalDateTime.now();
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