package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.OrderModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends CrdRepository<OrderModel> {

    List<OrderModel> findByUserId(long id, Limiter limiter);

    Optional<OrderModel> findByIds(long userId, long purchaseId);

    Optional<UserModel> findTheMostActiveUser();

}
