package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.PurchaseModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends CrdRepository<PurchaseModel> {

    List<PurchaseModel> findByUserId(long id, Limiter limiter);

    Optional<PurchaseModel> findByIds(long userId, long purchaseId);

    Optional<UserModel> findTheMostActiveUser();

    @Override
    default boolean delete(long id) {
        throw new UnsupportedOperationException();
    }

}
