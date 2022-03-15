package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.AccountRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleModel, Long> {

    Optional<AccountRoleModel> findByName(String roleName);

}
