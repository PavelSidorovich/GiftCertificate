package com.epam.esm.gcs.hateoas;

import com.epam.esm.gcs.controller.StatsController;
import com.epam.esm.gcs.controller.TagController;
import com.epam.esm.gcs.dto.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TagAssembler implements RepresentationModelAssembler<TagDto, EntityModel<TagDto>> {

    private static final String TAGS_REL = "tags";
    private static final String MOST_USED_TAG_REL = "mostUsedTag";
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;

    @Override
    public EntityModel<TagDto> toModel(TagDto tag) {
        return EntityModel.of(
                tag,
                linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel(),
                linkTo(methodOn(TagController.class).findAll(LIMIT, OFFSET)).withRel(TAGS_REL),
                linkTo(methodOn(StatsController.class).findMostUsedTag()).withRel(MOST_USED_TAG_REL)
        );
    }

    @Override
    public CollectionModel<EntityModel<TagDto>> toCollectionModel(Iterable<? extends TagDto> tags) {
        List<EntityModel<TagDto>> tagDtos = new ArrayList<>();
        tags.forEach(tag -> tagDtos.add(EntityModel.of(
                tag,
                linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel())
        ));
        return CollectionModel.of(
                tagDtos,
                linkTo(methodOn(StatsController.class).findMostUsedTag()).withRel(MOST_USED_TAG_REL),
                linkTo(methodOn(TagController.class).findAll(LIMIT, OFFSET)).withSelfRel()
        );
    }

}
