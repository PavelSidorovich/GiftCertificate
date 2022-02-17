package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.GiftCertificateModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class EntityFieldServiceImplTest {

    private final EntityFieldServiceImpl entityFieldService;

    @Autowired
    public EntityFieldServiceImplTest(EntityFieldServiceImpl entityFieldService) {
        this.entityFieldService = entityFieldService;
    }

    @ParameterizedTest
    @MethodSource("notNullModelProvider")
    void getNotNullFields_shouldReturnNotNullField_always(
            GiftCertificateModel certificate, List<String> expected) {
        final List<String> actual = entityFieldService.getNotNullFields(certificate);

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
    }

    private static Stream<Arguments> notNullModelProvider() {
        return Stream.of(
                Arguments.of(getModel1(), List.of("Id", "Name")),
                Arguments.of(getModel2(), List.of("Name", "Description", "Duration")),
                Arguments.of(getModel3(), Collections.emptyList())
        );
    }

    @ParameterizedTest
    @MethodSource("notNullWithExcludeModelProvider")
    void testGetNotNullFields_shouldReturnNotNullFieldsExcludingChosen_always(
            GiftCertificateModel certificate, List<String> expected, int expectedSize) {
        final List<String> actual = entityFieldService.getNotNullFields(certificate, "id", "name");

        assertEquals(expectedSize, actual.size());
        assertTrue(expected.containsAll(actual));
    }

    private static Stream<Arguments> notNullWithExcludeModelProvider() {
        return Stream.of(
                Arguments.of(getModel1(), List.of("Id", "Name"), 0),
                Arguments.of(getModel2(), List.of("Name", "Description", "Duration"), 2),
                Arguments.of(getModel3(), Collections.emptyList(), 0)
        );
    }

    private static GiftCertificateModel getModel1() {
        return GiftCertificateModel.builder()
                                   .id(1L)
                                   .name("test")
                                   .build();
    }

    private static GiftCertificateModel getModel2() {
        return GiftCertificateModel.builder()
                                   .name("test")
                                   .duration(10)
                                   .description("desc")
                                   .build();
    }

    private static GiftCertificateModel getModel3() {
        return GiftCertificateModel.builder()
                                   .build();
    }

}