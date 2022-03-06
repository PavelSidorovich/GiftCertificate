package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.AccountModel;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.OrderModel_;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.OrderRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.OrderService;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final GiftCertificateService certificateService;
    private final ModelMapper modelMapper;

    /**
     * Creates order model
     *
     * @param userId        user id
     * @param certificateId certificate id to buy
     * @return saved order
     */
    @Override
    @Transactional
    public OrderDto purchase(long userId, long certificateId) {
        OrderDto orderDto = new OrderDto();
        UserDto user = userService.findById(userId);
        GiftCertificateDto certificate = certificateService.findById(certificateId);

        orderDto.setUser(user);
        orderDto.setCertificate(certificate);
        checkBalance(certificate, user);

        OrderModel orderModel = orderRepository.save(modelMapper.map(orderDto, OrderModel.class));
        return modelMapper.map(orderModel, OrderDto.class);
    }

    /**
     * Finds user orders
     *
     * @param userId   user id
     * @param pageable pagination
     * @return user orders
     */
    @Override
    public List<OrderDto> findByUserId(long userId, Pageable pageable) {
        userService.findById(userId); // checks if user exists (throws exception if not)
        return orderRepository.findByUserId(userId, pageable).stream()
                              .map(order -> modelMapper.map(order, OrderDto.class))
                              .collect(Collectors.toList());
    }

    /**
     * Finds truncated order by user and order ids
     *
     * @param userId  user id
     * @param orderId order id
     * @return truncated order (contains only id, cost and purchase date)
     */
    @Override
    public TruncatedOrderDto findTruncatedByIds(long userId, long orderId) {
        userService.findById(userId); // checks if user exists (throws exception if not)
        OrderModel certificate = orderRepository.findByUserIdAndId(userId, orderId).orElseThrow(
                () -> new EntityNotFoundException(OrderDto.class, OrderModel_.ID, orderId)
        );
        return modelMapper.map(certificate, TruncatedOrderDto.class);
    }

    // TODO: 2/27/2022 edit

    /**
     * Finds the most widely used tag of the most active user
     *
     * @return the most widely user tag of a user
     */
//    @Override
//    public TagDto findMostWidelyTag() {
//        UserDto user = userService.findTheMostActiveUser();
//
//        Optional<TagModel> tag = findWidelyUsedTag(modelMapper.map(user, AccountModel.class));
//        if (tag.isPresent()) {
//            return modelMapper.map(tag.get(), TagDto.class);
//        }
//        throw new NoWidelyUsedTagException(TagDto.class);
//    }

    private Optional<TagModel> findWidelyUsedTag(AccountModel user) {
        return orderRepository.findByUserId(user.getId(), Pageable.unpaged()).stream()
                              .map(orderModel -> orderModel.getCertificate()
                                                           .getTags())
                              .flatMap(Set::stream)
                              .collect(Collectors.groupingBy(Function.identity(),
                                                             Collectors.counting()))
                              .entrySet()
                              .stream()
                              .max(Entry.comparingByValue())
                              .map(Entry::getKey);
    }

    private void checkBalance(GiftCertificateDto certificate, UserDto userDto) {
        final BigDecimal userDtoBalance = userDto.getBalance();
        final BigDecimal certificateCost = certificate.getPrice();

        if (userDtoBalance.subtract(certificateCost).compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(
                    OrderDto.class, certificate.getName(),
                    certificateCost, userDtoBalance
            );
        }
    }

}
