package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.model.CertificateModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository
        extends JpaRepository<CertificateModel, Long>,
                JpaSpecificationExecutor<CertificateModel> {

    @Query("SELECT DISTINCT c FROM CertificateModel c " +
           "JOIN c.tags as t " +
           "WHERE lower(t.name) IN ?1 " +
           "group by c.id, c.name, c.price, c.duration, " +
           "c.description, c.createDate, c.lastUpdateDate")
    List<CertificateModel> findByTags(List<String> tags, Pageable pageable);

    Optional<CertificateModel> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
