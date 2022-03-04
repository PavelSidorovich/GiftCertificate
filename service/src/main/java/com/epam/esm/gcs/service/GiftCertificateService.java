package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GiftCertificateService
        extends CrudService<GiftCertificateDto> {

    List<GiftCertificateDto> findByFilter(CertificateSearchCriteria searchCriteria);

    List<GiftCertificateDto> findByTags(List<String> tags, Pageable pageable);

}
