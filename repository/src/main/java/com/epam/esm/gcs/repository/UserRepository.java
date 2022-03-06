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

    // FIXME: 3/6/2022
//    @Query(value = "SELECT u.id, email, account_password, is_enabled, first_name, last_name balance " +
//                   "FROM ( " +
//                   "         SELECT t.user_id, SUM(t.total_cost) " +
//                   "         FROM user_order t " +
//                   "         group by t.user_id " +
//                   "         order by SUM(t.total_cost) DESC " +
//                   "         LIMIT 1 " +
//                   "     ) as m " +
//                   "INNER JOIN user_detail u ON u.id = m.user_id " +
//                   "INNER JOIN account a ON a.id = m.user_id", nativeQuery = true)
//    Optional<UserModel> findTheMostActiveUser();

}
