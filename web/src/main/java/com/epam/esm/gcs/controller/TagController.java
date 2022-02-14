package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.service.TagService;
import com.epam.esm.gcs.util.Limiter;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Order(value = 1)
public class TagController {

    private final TagService tagService;

    @GetMapping
    private List<TagDto> findAll(@RequestParam(required = false) Integer limit,
                                 @RequestParam(required = false) Integer offset) {
        Limiter limiter = new QueryLimiter(limit, offset);
        return tagService.findAll(limiter);
    }

    @GetMapping("/{id}")
    private TagDto findById(@PathVariable long id) {
        return tagService.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    private TagDto create(@Valid @RequestBody TagDto tag) {
        return tagService.create(tag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void delete(@PathVariable long id) {
        tagService.delete(id);
    }

}
