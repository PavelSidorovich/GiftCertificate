package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface GiftCertificateService
        extends CrudService<GiftCertificateDto> {

    GiftCertificateDto findByName(String name);

    List<GiftCertificateDto> findByFilter(GiftCertificateDto certificate,
                                          String sortByCreatedDate, String sortByName);

    List<GiftCertificateDto> findByTags(List<String> tags, Limiter limiter);

}
