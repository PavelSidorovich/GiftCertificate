package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.GiftCertificateModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCertificateRepository
        extends JpaRepository<GiftCertificateModel, Long>,
                JpaSpecificationExecutor<GiftCertificateModel> {

    @Query("SELECT DISTINCT c FROM GiftCertificateModel c " +
           "JOIN c.tags as t " +
           "WHERE lower(t.name) IN ?1 " +
           "group by c.id, c.name, c.price, c.duration, " +
           "c.description, c.createDate, c.lastUpdateDate " +
           "having count (t.id) = ?2")
    List<GiftCertificateModel> findByTags(List<String> tags, long size, Pageable pageable);

    Optional<GiftCertificateModel> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
