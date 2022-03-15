package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.TagController;
import com.epam.esm.gcs.controller.UserController;
import com.epam.esm.gcs.dto.UserDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    private static final String USERS_REL = "users";
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;
    private static final long PURCHASE_ID = 1L;

    @Override
    public EntityModel<UserDto> toModel(UserDto user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll(LIMIT, OFFSET)).withRel(USERS_REL),
                linkTo(methodOn(UserController.class).findUserOrders(user.getId(), LIMIT, OFFSET))
                        .withRel(USERS_REL),
                linkTo(methodOn(UserController.class).getTruncatedOrderInfo(user.getId(), PURCHASE_ID))
                        .withRel(USERS_REL)
        );
    }

    @Override
    public CollectionModel<EntityModel<UserDto>> toCollectionModel(Iterable<? extends UserDto> users) {
        List<EntityModel<UserDto>> userDtos = new ArrayList<>();
        users.forEach(user -> userDtos.add(EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel())
        ));
        return CollectionModel.of(
                userDtos,
                linkTo(methodOn(TagController.class).findAll(LIMIT, OFFSET)).withSelfRel()
        );
    }

}
