package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;

public interface GiftCertificateService
        extends CrudService<GiftCertificateDto> {

    GiftCertificateDto findByName(String name);

}
