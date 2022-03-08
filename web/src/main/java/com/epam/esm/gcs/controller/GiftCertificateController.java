package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.hateoas.GiftCertificateAssembler;
import com.epam.esm.gcs.service.CertificateService;
import com.epam.esm.gcs.spec.CertificateSearchCriteria;
import com.epam.esm.gcs.util.PageRequestFactoryService;
import com.epam.esm.gcs.validator.CreateValidationGroup;
import com.epam.esm.gcs.validator.UpdateValidationGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class GiftCertificateController {

    private final CertificateService certificateService;
    private final GiftCertificateAssembler certificateAssembler;
    private final PageRequestFactoryService paginationFactory;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<CertificateDto> create(
            @Validated({ CreateValidationGroup.class })
            @RequestBody CertificateDto certificate) {
        return certificateAssembler.toModel(certificateService.create(certificate));
    }

    @GetMapping
    public CollectionModel<EntityModel<CertificateDto>> findByFilter(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String certName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String sortByCreateDate,
            @RequestParam(required = false) String sortByName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        CertificateSearchCriteria searchCriteria = new CertificateSearchCriteria(
                paginationFactory.pageable(page, size), certName,
                tagName, description, sortByCreateDate, sortByName
        );
        return certificateAssembler.toCollectionModel(
                certificateService.findByFilter(searchCriteria)
        );
    }

    @GetMapping(params = "tag")
    public CollectionModel<EntityModel<CertificateDto>> findByTags(
            @RequestParam List<String> tag,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return certificateAssembler.toCollectionModel(
                certificateService.findByTags(tag, paginationFactory.pageable(page, size))
        );
    }

    @GetMapping(value = "/{id}")
    public EntityModel<CertificateDto> findById(@PathVariable long id) {
        return certificateAssembler.toModel(certificateService.findById(id));
    }

    @PatchMapping("/{id}")
    public EntityModel<CertificateDto> update(
            @Validated({ UpdateValidationGroup.class })
            @RequestBody CertificateDto certificate,
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
