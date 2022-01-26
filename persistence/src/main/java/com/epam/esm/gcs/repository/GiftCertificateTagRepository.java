package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.GiftCertificateModel;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateTagRepository extends ModelLinker<GiftCertificateModel> {

    Optional<GiftCertificateModel> findByName(String name);

    List<Long> findIdsByFilter(GiftCertificateModel certificate);

}
