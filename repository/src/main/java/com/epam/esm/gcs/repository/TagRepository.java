package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {

    Optional<TagModel> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT tag_id as id, ct.name as name\n" +
                   "FROM user_order\n" +
                   "         inner join certificate c on c.id = user_order.certificate_id\n" +
                   "         inner join certificates_by_tags cbt on c.id = cbt.certificate_id\n" +
                   "         inner join certificate_tag ct on cbt.tag_id = ct.id\n" +
                   "where user_id = (SELECT user_id\n" +
                   "                 FROM (SELECT user_id\n" +
                   "                       FROM user_order u\n" +
                   "                       group by user_id\n" +
                   "                       order by sum(total_cost) desc\n" +
                   "                       limit 1) as s)\n" +
                   "group by tag_id, ct.name\n" +
                   "order by count(tag_id) desc\n" +
                   "limit 1\n", nativeQuery = true)
    Optional<TagModel> findTheMostUsedTag();

}
