package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.GiftCertificateController;
import com.epam.esm.gcs.controller.TagController;
import com.epam.esm.gcs.dto.GiftCertificateDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// TODO: 2/16/2022 filtering, sorting
@Component
public class GiftCertificateAssembler
        implements RepresentationModelAssembler<GiftCertificateDto, EntityModel<GiftCertificateDto>> {

    private static final String CERTIFICATES_WITH_TAGS_REL = "certificatesWithTags";
    private static final String CERTIFICATES_REL = "certificates";
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;

    @Override
    public EntityModel<GiftCertificateDto> toModel(GiftCertificateDto certificate) {
        return EntityModel.of(
                certificate,
                linkTo(methodOn(GiftCertificateController.class).findById(certificate.getId())).withSelfRel(),
                linkTo(methodOn(GiftCertificateController.class).findByTags(
                        List.of("gift", "self-development"), LIMIT, OFFSET)).withRel(CERTIFICATES_WITH_TAGS_REL),
                linkTo(methodOn(GiftCertificateController.class).findAll(LIMIT, OFFSET)).withRel(CERTIFICATES_REL)
        );
    }

    @Override
    public CollectionModel<EntityModel<GiftCertificateDto>> toCollectionModel(
            Iterable<? extends GiftCertificateDto> certificates) {
        List<EntityModel<GiftCertificateDto>> certificateDtos = new ArrayList<>();
        certificates.forEach(cert -> certificateDtos.add(EntityModel.of(
                cert,
                linkTo(methodOn(TagController.class).findById(cert.getId())).withSelfRel())
        ));
        return CollectionModel.of(
                certificateDtos,
                linkTo(methodOn(TagController.class).findAll(LIMIT, OFFSET)).withSelfRel()
        );
    }

}
