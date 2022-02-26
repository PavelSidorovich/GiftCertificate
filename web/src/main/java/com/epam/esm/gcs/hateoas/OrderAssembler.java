package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.UserController;
import com.epam.esm.gcs.dto.OrderDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrderAssembler implements RepresentationModelAssembler<OrderDto, EntityModel<OrderDto>> {

    private static final int LIMIT = 10;
    private static final int OFFSET = 0;
    private static final String USER_ORDERS_REL = "userPurchases";
    private static final String ORDER_INFO_REL = "purchaseInfo";

    @Override
    public EntityModel<OrderDto> toModel(OrderDto order) {
        long userId = order.getUser().getId();
        long certificateId = order.getCertificate().getId();
        return EntityModel.of(
                order,
                linkTo(methodOn(UserController.class).findUserOrders(userId, LIMIT, OFFSET)).withRel(
                        USER_ORDERS_REL),
                linkTo(methodOn(UserController.class).getTruncatedOrderInfo(userId, order.getId())).withRel(
                        ORDER_INFO_REL),
                linkTo(methodOn(UserController.class).makePurchase(certificateId, userId)).withSelfRel()
        );
    }

    @Override
    public CollectionModel<EntityModel<OrderDto>> toCollectionModel(Iterable<? extends OrderDto> orders) {
        List<EntityModel<OrderDto>> orderDtos = new ArrayList<>();
        orders.forEach(order -> orderDtos.add(EntityModel.of(
                order,
                linkTo(methodOn(UserController.class).getTruncatedOrderInfo(
                        order.getUser().getId(), order.getId())
                ).withRel(ORDER_INFO_REL)))
        );
        if (orderDtos.isEmpty()) {
            return CollectionModel.of(orderDtos);
        } else {
            long userId = Objects.requireNonNull(orderDtos.get(0).getContent()).getUser().getId();
            return CollectionModel.of(
                    orderDtos,
                    linkTo(methodOn(UserController.class).findUserOrders(userId, LIMIT, OFFSET))
                            .withSelfRel()
            );
        }
    }

}
