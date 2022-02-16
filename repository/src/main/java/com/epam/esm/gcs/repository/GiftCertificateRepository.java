package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateRepository
        extends CrudRepository<GiftCertificateModel> {

    Optional<GiftCertificateModel> findByName(String name);

    List<GiftCertificateModel> findByTags(List<String> tags, Limiter limiter);

    List<GiftCertificateModel> findByFilter(GiftCertificateFilter filter, Limiter limiter);

    boolean existsWithName(String name);

}
