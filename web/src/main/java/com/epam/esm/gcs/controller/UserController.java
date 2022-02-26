package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TruncatedGiftCertificateDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.hateoas.PurchaseAssembler;
import com.epam.esm.gcs.hateoas.TruncatedPurchaseAssembler;
import com.epam.esm.gcs.hateoas.UserAssembler;
import com.epam.esm.gcs.service.PurchaseService;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;
    private final UserAssembler userAssembler;
    private final PurchaseAssembler purchaseAssembler;
    private final TruncatedPurchaseAssembler truncatedPurchaseAssembler;

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return userAssembler.toCollectionModel(userService.findAll(new QueryLimiter(limit, offset)));
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> findById(@PathVariable long id) {
        return userAssembler.toModel(userService.findById(id));
    }

    @PostMapping("/{userId}/certificates")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<OrderDto> makePurchase(
            @Valid
            @RequestBody TruncatedGiftCertificateDto certificateDto,
            @PathVariable long userId) {
        return purchaseAssembler.toModel(purchaseService.purchase(userId, certificateDto));
    }

    @GetMapping("/{userId}/certificates")
    public CollectionModel<EntityModel<OrderDto>> findUserPurchases(
            @PathVariable long userId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return purchaseAssembler.toCollectionModel(
                purchaseService.findByUserId(userId, new QueryLimiter(limit, offset))
        );
    }

    @GetMapping("/{userId}/certificates/{purchaseId}")
    public EntityModel<TruncatedOrderDto> getTruncatedPurchaseInfo(
            @PathVariable long userId,
            @PathVariable long purchaseId) {
        return truncatedPurchaseAssembler.toModel(purchaseService.findTruncatedByIds(userId, purchaseId));
    }

}
