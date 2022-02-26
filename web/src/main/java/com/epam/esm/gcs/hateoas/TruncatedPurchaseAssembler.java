package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.UserController;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TruncatedPurchaseAssembler
        implements RepresentationModelAssembler<TruncatedOrderDto, EntityModel<TruncatedOrderDto>> {

    private static final long USER_ID = 1L;

    @Override
    public EntityModel<TruncatedOrderDto> toModel(TruncatedOrderDto truncatedPurchase) {
        return EntityModel.of(
                truncatedPurchase,
                linkTo(methodOn(UserController.class).getTruncatedOrderInfo(USER_ID, truncatedPurchase.getId()))
                        .withSelfRel()
        );
    }

}
