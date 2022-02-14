package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class StatsController {

    private final PurchaseService purchaseService;

    @GetMapping(value = "/tags")
    private TagDto findMostUsedTags() {
        return purchaseService.findMostWidelyTag();
    }

}
