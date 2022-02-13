package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.UserModel;

import java.util.Optional;

public interface UserRepository extends CrdRepository<UserModel> {

    Optional<UserModel> findByEmail(String email);

    boolean existsWithEmail(String email);

}
