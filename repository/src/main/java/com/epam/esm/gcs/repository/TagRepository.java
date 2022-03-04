package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {

    Optional<TagModel> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
