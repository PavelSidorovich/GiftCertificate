package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;

import java.util.List;

public interface GiftCertificateTagDtoRepository
        extends ModelLinkerService<GiftCertificateDto> {

    GiftCertificateDto findByName(String name);

    List<GiftCertificateDto> findByFilter(GiftCertificateDto certificate,
                                          String sortByCreatedDate, String sortByName);

}
