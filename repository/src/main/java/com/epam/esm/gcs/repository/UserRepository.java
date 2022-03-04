package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.UserModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public interface UserRepository extends PagingAndSortingRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT id, first_name, last_name, email, balance, account_password " +
                   "FROM ( " +
                   "         SELECT t.user_id, SUM(t.total_cost) " +
                   "         FROM user_order t " +
                   "         group by t.user_id " +
                   "         order by SUM(t.total_cost) DESC " +
                   "         LIMIT 1 " +
                   "     ) as m " +
                   "INNER JOIN user_account u ON u.id = m.user_id", nativeQuery = true)
    Optional<UserModel> findTheMostActiveUser();

}
