package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import com.epam.esm.gcs.validator.CreateValidationGroup;
import com.epam.esm.gcs.validator.UpdateValidationGroup;
import lombok.RequiredArgsConstructor;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificateDto create(@Validated({ CreateValidationGroup.class })
                                     @RequestBody GiftCertificateDto certificate) {
        return certificateService.create(certificate);
    }

    @GetMapping
    public List<GiftCertificateDto> findAll(@RequestParam(required = false) Integer limit,
                                            @RequestParam(required = false) Integer offset) {
        return certificateService.findAll(new QueryLimiter(limit, offset));
    }

    @GetMapping(value = "/{id}")
    public GiftCertificateDto findById(@PathVariable long id) {
        return certificateService.findById(id);
    }

    @PatchMapping
    public GiftCertificateDto update(@Validated({ UpdateValidationGroup.class })
                                     @RequestBody GiftCertificateDto certificate) {
        return certificateService.update(certificate);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        certificateService.delete(id);
    }

}
