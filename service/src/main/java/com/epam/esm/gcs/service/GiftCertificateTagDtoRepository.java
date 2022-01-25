package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;

public interface GiftCertificateTagDtoRepository
        extends ModelLinkerService<GiftCertificateDto> {

    GiftCertificateDto findByName(String name);

}
