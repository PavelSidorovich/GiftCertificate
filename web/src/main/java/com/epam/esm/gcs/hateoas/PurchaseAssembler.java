package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.UserController;
import com.epam.esm.gcs.dto.PurchaseDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PurchaseAssembler implements RepresentationModelAssembler<PurchaseDto, EntityModel<PurchaseDto>> {

    private static final int LIMIT = 10;
    private static final int OFFSET = 0;
    private static final String USER_PURCHASES_REL = "userPurchases";
    private static final String PURCHASE_INFO_REL = "purchaseInfo";

    @Override
    public EntityModel<PurchaseDto> toModel(PurchaseDto purchase) {
        long userId = purchase.getUser().getId();
        return EntityModel.of(
                purchase,
                linkTo(methodOn(UserController.class).findUserPurchases(userId, LIMIT, OFFSET)).withRel(
                        USER_PURCHASES_REL),
                linkTo(methodOn(UserController.class).getTruncatedPurchaseInfo(userId, purchase.getId())).withRel(
                        PURCHASE_INFO_REL),
                linkTo(methodOn(UserController.class).makePurchase(purchase, userId)).withSelfRel()
        );
    }

    @Override
    public CollectionModel<EntityModel<PurchaseDto>> toCollectionModel(Iterable<? extends PurchaseDto> purchases) {
        List<EntityModel<PurchaseDto>> purchaseDtos = new ArrayList<>();
        purchases.forEach(purchase -> purchaseDtos.add(EntityModel.of(
                purchase,
                linkTo(methodOn(UserController.class).getTruncatedPurchaseInfo(
                        purchase.getUser().getId(), purchase.getId())
                ).withRel(PURCHASE_INFO_REL)))
        );
        if (purchaseDtos.isEmpty()) {
            return CollectionModel.of(purchaseDtos);
        } else {
            long userId = Objects.requireNonNull(purchaseDtos.get(0).getContent()).getUser().getId();
            return CollectionModel.of(
                    purchaseDtos,
                    linkTo(methodOn(UserController.class).findUserPurchases(userId, LIMIT, OFFSET))
                            .withSelfRel()
            );
        }
    }

}
