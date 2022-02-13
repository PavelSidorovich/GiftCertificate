package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.TagModel;

import java.util.Optional;

public interface TagRepository extends CrdRepository<TagModel> {

    Optional<TagModel> findByName(String name);

    boolean existsWithName(String name);

}
