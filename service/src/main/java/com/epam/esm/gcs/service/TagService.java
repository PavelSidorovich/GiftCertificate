package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.TagDto;

public interface TagService extends CrdService<TagDto> {

    TagDto findByName(String name);

    boolean existsWithName(String name);

}
