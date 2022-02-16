package com.epam.esm.gcs.filter;

import lombok.Data;

@Data
public class GiftCertificateFilter {

    private String certificateName;
    private String tagName;
    private String description;
    private SortType sortCreateDateType;
    private SortType sortNameType;

    public GiftCertificateFilter(String certificateName, String tagName, String description,
                                 String sortCreateDateType, String sortNameType) {
        this.certificateName = certificateName;
        this.tagName = tagName;
        this.description = description;
        this.sortCreateDateType = sortCreateDateType != null? SortType.getSortType(sortCreateDateType) : SortType.NONE;
        this.sortNameType = sortNameType != null? SortType.getSortType(sortNameType) : SortType.NONE;
    }

}
