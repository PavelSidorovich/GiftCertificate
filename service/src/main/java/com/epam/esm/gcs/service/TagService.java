package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.TagDto;

public interface TagService extends CrdService<TagDto> {

    boolean existsWithName(String name);

    TagDto findByName(String name);

}
