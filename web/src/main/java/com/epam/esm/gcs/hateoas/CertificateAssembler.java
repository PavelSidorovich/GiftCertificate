package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.CertificateController;
import com.epam.esm.gcs.controller.TagController;
import com.epam.esm.gcs.dto.CertificateDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CertificateAssembler
        implements RepresentationModelAssembler<CertificateDto, EntityModel<CertificateDto>> {

    private static final String CERTIFICATES_WITH_TAGS_REL = "certificatesWithTags";
    private static final String CERTIFICATES_REL = "certificates";
    private static final String TAGS_REL = "tags";
    private static final String TAG_REL = "tag";
    private static final String TAG_NAME_1 = "gift";
    private static final String CERT_NAME = "swimming pool";
    private static final String DESCRIPTION = "10% discount";
    private static final String SORT_BY_CREATED_DATE = "DESC";
    private static final String SORT_BY_NAME = "ASC";
    private static final String TAG_NAME_2 = "self-development";
    private static final int PAGE = 0;
    private static final int PAGE_SIZE = 10;

    @Override
    public EntityModel<CertificateDto> toModel(CertificateDto certificate) {
        return EntityModel.of(
                certificate,
                linkTo(methodOn(CertificateController.class).findById(certificate.getId())).withSelfRel(),
                linkTo(methodOn(CertificateController.class).findByTags(
                        List.of(TAG_NAME_1, TAG_NAME_2), PAGE, PAGE_SIZE)).withRel(CERTIFICATES_WITH_TAGS_REL),
                linkTo(methodOn(CertificateController.class).findByFilter(
                        TAG_NAME_1, CERT_NAME, DESCRIPTION, SORT_BY_CREATED_DATE, SORT_BY_NAME, PAGE, PAGE_SIZE
                )).withRel(CERTIFICATES_REL)
        );
    }

    @Override
    public CollectionModel<EntityModel<CertificateDto>> toCollectionModel(
            Iterable<? extends CertificateDto> certificates) {
        List<EntityModel<CertificateDto>> certificateDtos = new ArrayList<>();
        certificates.forEach(cert -> certificateDtos.add(EntityModel.of(
                cert,
                linkTo(methodOn(TagController.class).findById(cert.getId())).withRel(TAG_REL))
        ));
        return CollectionModel.of(
                certificateDtos,
                linkTo(methodOn(TagController.class).findAll(PAGE, PAGE_SIZE)).withRel(TAGS_REL),
                linkTo(methodOn(CertificateController.class).findByTags(
                        List.of(TAG_NAME_1, TAG_NAME_2), PAGE, PAGE_SIZE)).withRel(CERTIFICATES_WITH_TAGS_REL),
                linkTo(methodOn(CertificateController.class).findByFilter(
                        TAG_NAME_1, CERT_NAME, DESCRIPTION, SORT_BY_CREATED_DATE, SORT_BY_NAME, PAGE, PAGE_SIZE
                )).withRel(CERTIFICATES_REL)
        );
    }

}
