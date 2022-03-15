package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.hateoas.TagAssembler;
import com.epam.esm.gcs.service.TagService;
import com.epam.esm.gcs.util.PageRequestFactoryService;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

@RestController
@RequestMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Order(value = 1)
public class TagController {

    private final TagService tagService;
    private final TagAssembler tagAssembler;
    private final PageRequestFactoryService pageRequestFactory;

    @GetMapping
    public CollectionModel<EntityModel<TagDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return tagAssembler.toCollectionModel(
                tagService.findAll(pageRequestFactory.pageable(page, size))
        );
    }

    @GetMapping("/{id}")
    public EntityModel<TagDto> findById(@PathVariable long id) {
        return tagAssembler.toModel(tagService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TagDto> create(@Valid @RequestBody TagDto tag) {
        return tagAssembler.toModel(tagService.create(tag));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        tagService.delete(id);
    }

}
