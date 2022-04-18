package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    Optional<AccountModel> findByEmail(String email);

    boolean existsByEmail(String email);

}
