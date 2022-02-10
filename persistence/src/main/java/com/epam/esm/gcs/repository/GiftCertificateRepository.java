package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.GiftCertificateModel;

import java.util.Optional;

public interface GiftCertificateRepository
        extends CrudRepository<GiftCertificateModel>, Flushable {

    boolean existsWithName(String name);

    Optional<GiftCertificateModel> findByName(String name);

}
