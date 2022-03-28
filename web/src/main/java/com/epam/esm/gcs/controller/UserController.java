package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.hateoas.OrderAssembler;
import com.epam.esm.gcs.hateoas.TruncatedPurchaseAssembler;
import com.epam.esm.gcs.hateoas.UserAssembler;
import com.epam.esm.gcs.service.OrderService;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.PageRequestFactoryService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final UserAssembler userAssembler;
    private final OrderAssembler orderAssembler;
    private final TruncatedPurchaseAssembler truncatedOrderAssembler;
    private final PageRequestFactoryService pageRequestFactory;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CollectionModel<EntityModel<UserDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return userAssembler.toCollectionModel(
                userService.findAll(pageRequestFactory.pageable(page, size))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("authentication.principal.id == #id or hasRole('ADMIN')")
    public EntityModel<UserDto> findById(@PathVariable long id) {
        return userAssembler.toModel(userService.findById(id));
    }

    @PostMapping("/{userId}/certificates/{certificateId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public EntityModel<OrderDto> makePurchase(
            @PathVariable long certificateId,
            @PathVariable long userId) {
        return orderAssembler.toModel(orderService.purchase(userId, certificateId));
    }

    @GetMapping("/{userId}/orders")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public CollectionModel<EntityModel<OrderDto>> findUserOrders(
            @PathVariable long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return orderAssembler.toCollectionModel(
                orderService.findByUserId(userId, pageRequestFactory.pageable(page, pageSize))
        );
    }

    @GetMapping("/{userId}/orders/{orderId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')")
    public EntityModel<TruncatedOrderDto> getTruncatedOrderInfo(
            @PathVariable long userId,
            @PathVariable long orderId) {
        return truncatedOrderAssembler.toModel(orderService.findTruncatedByIds(userId, orderId));
    }

}
