package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.TagModel;

public interface TagRepository extends CRDRepository<TagModel> {

    boolean existsWithName(String name);

}
