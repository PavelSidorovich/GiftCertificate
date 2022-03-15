package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CertificateService
        extends CrudService<CertificateDto> {

    List<CertificateDto> findByFilter(CertificateSearchCriteria searchCriteria);

    List<CertificateDto> findByTags(List<String> tags, Pageable pageable);

}
