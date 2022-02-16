package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.UserController;
import com.epam.esm.gcs.dto.TruncatedPurchaseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TruncatedPurchaseAssembler
        implements RepresentationModelAssembler<TruncatedPurchaseDto, EntityModel<TruncatedPurchaseDto>> {

    private static final long USER_ID = 1L;

    @Override
    public EntityModel<TruncatedPurchaseDto> toModel(TruncatedPurchaseDto truncatedPurchase) {
        return EntityModel.of(
                truncatedPurchase,
                linkTo(methodOn(UserController.class).getTruncatedPurchaseInfo(USER_ID, truncatedPurchase.getId()))
                        .withSelfRel()
        );
    }

}
