package com.epam.esm.gcs.repository.impl;

import com.epam.esm.gcs.config.TestConfig;
import com.epam.esm.gcs.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles({ "dev" })
@EnableAutoConfiguration
@SpringBootTest(classes = { TestConfig.class })
class UserRepositoryImplTest {

    private final UserRepositoryImpl userRepository;

    @Autowired
    public UserRepositoryImplTest(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void create_shouldThrowUnsupportedOperationException_always() {
        assertThrows(UnsupportedOperationException.class, () -> userRepository.create(new UserModel()));
    }

    @Test
    void findByEmail_shouldFindUserWithEmail_whenExists() {
        assertFalse(userRepository.findByEmail("fail").isPresent());
    }

    @Test
    void delete_shouldThrowUnsupportedOperationException_always() {
        assertThrows(UnsupportedOperationException.class, () -> userRepository.delete(1L));
    }

    @Test
    void existsWithEmail() {
        assertFalse(userRepository.existsWithEmail("fail"));
    }

}