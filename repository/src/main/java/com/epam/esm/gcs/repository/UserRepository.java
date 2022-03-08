package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.UserModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public interface UserRepository extends PagingAndSortingRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

}
