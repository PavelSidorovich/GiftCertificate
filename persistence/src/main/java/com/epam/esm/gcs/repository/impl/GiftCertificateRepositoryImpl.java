package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.repository.GiftCertificateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public class GiftCertificateRepositoryImpl extends AbstractRepository<GiftCertificateModel>
        implements GiftCertificateRepository {

    private static final String FIND_BY_NAME_QUERY = "SELECT c FROM %s c WHERE c.name = ?1";

    private final BeanUtilsBean beanUtilsBean;

    public GiftCertificateRepositoryImpl(EntityManager entityManager,
                                         BeanUtilsBean beanUtilsBean) {
        super(entityManager);
        this.beanUtilsBean = beanUtilsBean;
    }

    /**
     * Creates new certificate
     *
     * @param model certificate to create
     * @return created certificate with generated id
     */
    @Override
    @Transactional
    public GiftCertificateModel create(GiftCertificateModel model) {
        final LocalDateTime currentTime = LocalDateTime.now();
        model.setCreateDate(currentTime);
        model.setLastUpdateDate(currentTime);
        if (model.getTags() != null) {
            model.setTags(attachTags(model.getTags()));
        }
        return super.create(model);
    }

    /**
     * Updates certificate with specified id
     *
     * @param model certificate to update. Should contain id
     * @return new version of certificate or Optional.empty if entity not found
     */
    @Override
    @Transactional
    public Optional<GiftCertificateModel> update(GiftCertificateModel model) {
        Optional<GiftCertificateModel> byId = findById(model.getId());
        if (byId.isPresent()) {
            try {
                GiftCertificateModel certificateToUpdate = byId.get();
                beanUtilsBean.copyProperties(certificateToUpdate, model);
                certificateToUpdate.setLastUpdateDate(LocalDateTime.now());
                if (model.getTags() != null) {
                    certificateToUpdate.setTags(attachTags(model.getTags()));
                }
                flushAndClear();
                return byId;
            } catch (InvocationTargetException | IllegalAccessException ex) {
                log.error("Certificate update error", ex);
            }
        }
        return Optional.empty();
    }

    private List<TagModel> attachTags(List<TagModel> tags) {
        return tags.stream()
                   .map(tag -> tag.getId() != null? entityManager.merge(tag) : tag)
                   .collect(Collectors.toList());
    }

    /**
     * Checks if certificate with specified name exists
     *
     * @param name name of certificate check for existence
     * @return true if exists, otherwise - false
     */
    @Override
    public boolean existsWithName(String name) {
        return findByName(name).isPresent();
    }

    /**
     * Finds certificate with specified name
     *
     * @param name name of certificate to find
     * @return Optional.empty if not found, certificate if found
     */
    @Override
    public Optional<GiftCertificateModel> findByName(String name) {
        return singleParamQuery(FIND_BY_NAME_QUERY, name);
    }

}
