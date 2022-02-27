package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.filter.GiftCertificateFilter;
import com.epam.esm.gcs.hateoas.GiftCertificateAssembler;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import com.epam.esm.gcs.validator.CreateValidationGroup;
import com.epam.esm.gcs.validator.UpdateValidationGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GiftCertificateController {

    private final GiftCertificateService certificateService;
    private final GiftCertificateAssembler certificateAssembler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<GiftCertificateDto> create(
            @Validated({ CreateValidationGroup.class })
            @RequestBody GiftCertificateDto certificate) {
        return certificateAssembler.toModel(certificateService.create(certificate));
    }

    @GetMapping
    public CollectionModel<EntityModel<GiftCertificateDto>> findByFilter(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String certName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String sortByCreatedDate,
            @RequestParam(required = false) String sortByName,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        if (tagName == null && certName == null && description == null
            && sortByCreatedDate == null && sortByName == null) {
            return certificateAssembler.toCollectionModel(
                    certificateService.findAll(new QueryLimiter(limit, offset))
            );
        } else {
            GiftCertificateFilter filter = new GiftCertificateFilter(
                    certName, tagName, description, sortByCreatedDate, sortByName
            );
            return certificateAssembler.toCollectionModel(
                    certificateService.findByFilter(filter, new QueryLimiter(limit, offset))
            );
        }
    }

    @GetMapping(params = "tag")
    public CollectionModel<EntityModel<GiftCertificateDto>> findByTags(
            @RequestParam List<String> tag,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return certificateAssembler.toCollectionModel(
                certificateService.findByTags(tag, new QueryLimiter(limit, offset))
        );
    }

    @GetMapping(value = "/{id}")
    public EntityModel<GiftCertificateDto> findById(@PathVariable long id) {
        return certificateAssembler.toModel(certificateService.findById(id));
    }

    @PatchMapping("/{id}")
    public EntityModel<GiftCertificateDto> update(
            @Validated({ UpdateValidationGroup.class })
            @RequestBody GiftCertificateDto certificate,
            @PathVariable long id) {
        certificate.setId(id);
        return certificateAssembler.toModel(certificateService.update(certificate));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        certificateService.delete(id);
    }

}
