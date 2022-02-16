package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface GiftCertificateService
        extends CrudService<GiftCertificateDto> {

    GiftCertificateDto findByName(String name);

    List<GiftCertificateDto> findByFilter(GiftCertificateFilter filter, Limiter limiter);

    List<GiftCertificateDto> findByTags(List<String> tags, Limiter limiter);

}
