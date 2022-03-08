package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class StatsController {

    private static final String TAGS_REL = "tags";
    private static final String TAG_REL = "tag";
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;

    private final TagService tagService;

    @GetMapping(value = "/tags")
    public EntityModel<TagDto> findTheMostUsedTag() {
        TagDto mostWidelyUsedTag = tagService.findTheMostUsedTag();
        return EntityModel.of(
                mostWidelyUsedTag,
                linkTo(methodOn(StatsController.class).findTheMostUsedTag()).withSelfRel(),
                linkTo(methodOn(TagController.class).findAll(LIMIT, OFFSET)).withRel(TAGS_REL),
                linkTo(methodOn(TagController.class).findById(mostWidelyUsedTag.getId())).withRel(TAG_REL)
        );
    }

}
