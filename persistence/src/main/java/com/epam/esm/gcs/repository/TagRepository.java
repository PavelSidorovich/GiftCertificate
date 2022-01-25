package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.TagModel;

import java.util.Optional;

public interface TagRepository extends CrdRepository<TagModel> {

    boolean existsWithName(String name);

    Optional<TagModel> findByName(String name);

}
