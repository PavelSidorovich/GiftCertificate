package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.PurchaseDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.service.PurchaseService;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.validator.PurchaseValidationGroup;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    @GetMapping
    private List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    private UserDto findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @PostMapping("/{id}/certificates")
    @ResponseStatus(HttpStatus.CREATED)
    private PurchaseDto makePurchase(@Validated(PurchaseValidationGroup.class)
                                 @RequestBody PurchaseDto purchaseDto,
                                 @PathVariable long id) {
        final UserDto user = new UserDto();
        user.setId(id);
        purchaseDto.setUser(user);
        return purchaseService.purchase(purchaseDto);
    }

}
