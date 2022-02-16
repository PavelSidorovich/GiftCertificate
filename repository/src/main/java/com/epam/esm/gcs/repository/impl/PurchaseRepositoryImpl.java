package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.GiftCertificateModel;
import com.epam.esm.gcs.model.PurchaseModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.PurchaseRepository;
import com.epam.esm.gcs.util.Limiter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public class PurchaseRepositoryImpl
        extends AbstractRepository<PurchaseModel>
        implements PurchaseRepository {

    private static final String FIND_ALL_BY_USER_ID_QUERY = "SELECT p FROM %s p WHERE p.user.id = ?1";
    private static final String FIND_BY_IDS_QUERY =
            "SELECT p FROM %s p WHERE p.user.id = ?1 AND p.id = ?2";
    private static final String FIND_THE_MOST_ACTIVE_USER =
            "SELECT id, first_name, last_name, email, balance\n" +
            "FROM (\n" +
            "         SELECT t.user_id, SUM(t.total_cost)\n" +
            "         FROM purchase t\n" +
            "         group by t.user_id\n" +
            "         order by SUM(t.total_cost) DESC\n" +
            "         LIMIT 1\n" +
            "     ) as m\n" +
            "INNER JOIN user_account u ON u.id = m.user_id";

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

        return super.create(model);
    }

    @Override
    public List<PurchaseModel> findByUserId(long id, Limiter limiter) {
        return listSingleParamQuery(FIND_ALL_BY_USER_ID_QUERY, id, limiter);
    }

    @Override
    public Optional<PurchaseModel> findByIds(long userId, long purchaseId) {
        final String sqlQuery = fillEntityClassInQuery(FIND_BY_IDS_QUERY);
        try {
            return Optional.ofNullable(
                    entityManager.createQuery(sqlQuery, entityBeanType)
                                 .setParameter(1, userId)
                                 .setParameter(2, purchaseId)
                                 .getSingleResult()
            );
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserModel> findTheMostActiveUser() {
        try {
            final String sqlQuery = fillEntityClassInQuery(FIND_THE_MOST_ACTIVE_USER);
            return Optional.of((UserModel) entityManager
                    .createNativeQuery(sqlQuery, UserModel.class)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(long id) {
        throw new UnsupportedOperationException();
    }

    private GiftCertificateModel getCertificateFromJPAContext(GiftCertificateModel certificate) {
        return entityManager.merge(certificate);
    }

    private UserModel getUserFromJPAContext(UserModel user) {
        return entityManager.merge(user);
    }

}
