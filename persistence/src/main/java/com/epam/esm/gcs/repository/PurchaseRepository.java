package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.PurchaseModel;

public interface PurchaseRepository extends CrdRepository<PurchaseModel> {

    @Override
    default boolean delete(long id){
        throw new UnsupportedOperationException();
    }

}
