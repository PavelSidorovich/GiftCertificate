package com.epam.esm.gcs.comparator.impl;

import com.epam.esm.gcs.comparator.ComparatorType;
import com.epam.esm.gcs.comparator.GiftCertificateComparatorFactory;
import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class GiftCertificateComparatorBuilderImplTest {

    private final GiftCertificateComparatorBuilderImpl comparatorBuilder;

    @Autowired
    public GiftCertificateComparatorBuilderImplTest(GiftCertificateComparatorFactory comparatorFactory) {
        this.comparatorBuilder = new GiftCertificateComparatorBuilderImpl(comparatorFactory);
    }

    @Test
    void buildComparator_shouldReturnNull_whenSortTypeIsInvalid() {
        final Map<ComparatorType, String> sortTypeByComparatorType = new HashMap<>();

        sortTypeByComparatorType.put(ComparatorType.NAME_COMPARATOR, "invalid");
        sortTypeByComparatorType.put(ComparatorType.CREATE_DATE_COMPARATOR, "invalid");

        Comparator<GiftCertificateDto> comparator = comparatorBuilder.buildComparator(sortTypeByComparatorType);

        assertNull(comparator);
    }

    @Test
    void buildComparator_shouldReturnDateComparator_whenSortTypeValid() {
        final Map<ComparatorType, String> sortTypeByComparatorType = new HashMap<>();

        sortTypeByComparatorType.put(ComparatorType.NAME_COMPARATOR, "ASC");
        sortTypeByComparatorType.put(ComparatorType.CREATE_DATE_COMPARATOR, "DESC");

        Comparator<GiftCertificateDto> comparator = comparatorBuilder.buildComparator(sortTypeByComparatorType);

        assertNotNull(comparator);
    }

}