package com.epam.esm.gcs.repository;

import com.epam.esm.gcs.config.TestConfig;
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
class UserRepositoryTest {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void findByEmail_shouldFindUserWithEmail_whenExists() {
        assertFalse(userRepository.findByEmail("fail").isPresent());
    }

    @Test
    void existsWithEmail() {
        assertFalse(userRepository.existsByEmail("fail"));
    }

}