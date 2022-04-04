package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.OrderModel_;
import com.epam.esm.gcs.repository.OrderRepository;
import com.epam.esm.gcs.service.CertificateService;
import com.epam.esm.gcs.service.OrderService;
import com.epam.esm.gcs.service.OrderTransactionService;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderTransactionService orderTransactionService;
    private final CertificateService certificateService;
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
        final OrderDto orderDto = new OrderDto();
        final CertificateDto certificate = certificateService.findById(certificateId);
        final UserDto user = orderTransactionService.withdrawMoney(userService.findById(userId), certificate);

        orderDto.setUser(user);
        orderDto.setCertificate(certificate);

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

}
