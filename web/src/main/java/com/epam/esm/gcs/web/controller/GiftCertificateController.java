package com.epam.esm.gcs.web.controller;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.service.GiftCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class GiftCertificateController {

    private final GiftCertificateService certificateService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificateDto create(@Valid @RequestBody GiftCertificateDto certificate) {
        return certificateService.create(certificate);
    }

    @GetMapping
    public List<GiftCertificateDto> findAll() {
        certificateService.findAll().forEach(System.out::println);
        return certificateService.findAll();
    }

    @GetMapping(value = "/{id}")
    public GiftCertificateDto findById(@PathVariable long id) {
        return certificateService.findById(id);
    }

    @PatchMapping
    public GiftCertificateDto update(@Valid @RequestBody GiftCertificateDto certificate) {
        return certificateService.update(certificate);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        certificateService.delete(id);
    }

}
