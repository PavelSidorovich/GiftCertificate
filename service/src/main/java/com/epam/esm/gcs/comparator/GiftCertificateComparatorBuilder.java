package com.epam.esm.gcs.comparator;

import com.epam.esm.gcs.dto.GiftCertificateDto;

import java.util.Comparator;
import java.util.Map;

public interface GiftCertificateComparatorBuilder {

    Comparator<GiftCertificateDto> buildComparator(Map<ComparatorType, String> sortByType);

}
