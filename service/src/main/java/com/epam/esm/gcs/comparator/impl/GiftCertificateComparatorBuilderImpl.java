package com.epam.esm.gcs.comparator.impl;

import com.epam.esm.gcs.comparator.ComparatorType;
import com.epam.esm.gcs.comparator.GiftCertificateComparatorBuilder;
import com.epam.esm.gcs.comparator.GiftCertificateComparatorFactory;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateComparatorBuilderImpl
        implements GiftCertificateComparatorBuilder {

    private static final String ASCENDING_SORT_TYPE = "ASC";

    private final GiftCertificateComparatorFactory comparatorFactory;

    @Override
    public Comparator<GiftCertificateDto> buildComparator(Map<ComparatorType, String> sortByType) {
        Comparator<GiftCertificateDto> comparator = null;

        for (Entry<ComparatorType, String> stringBooleanEntry : sortByType.entrySet()) {
            final ComparatorType comparatorType = stringBooleanEntry.getKey();
            final Boolean ascSort = isAscendingSort(stringBooleanEntry.getValue());
            Comparator<GiftCertificateDto> comparatorToAdd = getComparatorToAdd(comparatorType, ascSort);
            comparator = comparator != null
                    ? comparator.thenComparing(comparatorToAdd)
                    : comparatorToAdd;
        }
        return comparator;
    }

    private Comparator<GiftCertificateDto> getComparatorToAdd(ComparatorType comparatorType, Boolean ascSort) {
        Comparator<GiftCertificateDto> comparatorToAdd = null;
        if (ascSort != null) {
            comparatorToAdd = ascSort
                    ? comparatorFactory.getComparator(comparatorType)
                    : comparatorFactory.getComparator(comparatorType).reversed();
        }
        return comparatorToAdd;
    }

    private Boolean isAscendingSort(String sortType) {
        if (sortType != null) {
            return ASCENDING_SORT_TYPE.equalsIgnoreCase(sortType);
        }
        return null;
    }

}
