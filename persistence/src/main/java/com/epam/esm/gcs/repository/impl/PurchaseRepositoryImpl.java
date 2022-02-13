package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.PurchaseModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.PurchaseRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public class PurchaseRepositoryImpl
        extends AbstractRepository<PurchaseModel>
        implements PurchaseRepository {

    public PurchaseRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    @Transactional
    public PurchaseModel create(PurchaseModel model) {
        final GiftCertificateModel certificate =
                getCertificateFromJPAContext(model.getCertificate());
        final UserModel detachedUser = model.getUser();
        detachedUser.setBalance(detachedUser.getBalance().subtract(certificate.getPrice()));
        final UserModel user = getUserFromJPAContext(detachedUser);

        model.setUser(user);
        model.setCertificate(certificate);
        model.setCost(certificate.getPrice());
        model.setPurchaseDate(LocalDateTime.now());

        return super.create(model);
    }

    private GiftCertificateModel getCertificateFromJPAContext(GiftCertificateModel certificate) {
        return entityManager.merge(certificate);
    }

    private UserModel getUserFromJPAContext(UserModel user) {
        return entityManager.merge(user);
    }

}
