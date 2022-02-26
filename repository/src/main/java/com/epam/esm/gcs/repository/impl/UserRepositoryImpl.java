package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.UserRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@EntityScan(basePackages = { "com.epam.esm.gcs.model" })
public class UserRepositoryImpl
        extends AbstractRepository<UserModel>
        implements UserRepository {

    private static final String FIND_BY_EMAIL_QUERY = "SELECT u FROM %s u WHERE u.email = ?1";

    public UserRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Unsupported operation
     */
    @Override
    public UserModel create(UserModel model) {
        throw new UnsupportedOperationException();
    }

    /**
     * Finds user by email
     *
     * @param email user email
     * @return user if such exists, otherwise — Optional.empty()
     */
    @Override
    public Optional<UserModel> findByEmail(String email) {
        return singleParamQuery(FIND_BY_EMAIL_QUERY, email);
    }

    /**
     * Unsupported operation
     */
    @Override
    public boolean delete(long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if user with email exists
     *
     * @param email user email
     * @return true if exists, false if not
     */
    @Override
    public boolean existsWithEmail(String email) {
        return findByEmail(email).isPresent();
    }

}
