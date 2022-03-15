package com.epam.esm.gcs.spec;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;

@Data
@EqualsAndHashCode(callSuper = true)
public class CertificateSearchCriteria extends AbstractSearchCriteria {

    private String certificateName;
    private String tagName;
    private String description;
    private SortType sortCreateDateType;
    private SortType sortNameType;

    public CertificateSearchCriteria(
            Pageable pageable, String certificateName,
            String tagName, String description,
            String sortCreateDateType, String sortNameType) {
        super(pageable.getPageNumber(), pageable.getPageSize());
        this.certificateName = certificateName;
        this.tagName = tagName;
        this.description = description;
        this.sortCreateDateType = sortCreateDateType != null
                ? SortType.getSortType(sortCreateDateType) : SortType.NONE;
        this.sortNameType = sortNameType != null
                ? SortType.getSortType(sortNameType) : SortType.NONE;
    }

}
