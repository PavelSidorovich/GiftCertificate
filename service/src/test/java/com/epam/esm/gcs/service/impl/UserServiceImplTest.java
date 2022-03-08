package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.AccountRepository;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.service.AccountRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// FIXME: 3/6/2022 edit tests
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserServiceImpl userService;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleService accountRoleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImplTest(@Mock UserRepository userRepository,
                               @Mock AccountRepository accountRepository,
                               @Mock AccountRoleService accountRoleService,
                               @Mock PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountRoleService = accountRoleService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = new ModelMapper();
        this.userService = new UserServiceImpl(
                userRepository, accountRepository,
                accountRoleService, passwordEncoder, modelMapper);
    }

    @Test
    void findById_shouldFindUserById_ifExists() {
        final long userId = 1L;
        UserModel user = new UserModel(
                userId, "email", "pass", true,
                "fName", "lName", BigDecimal.TEN, Collections.emptySet());
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actual = userService.findById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_ifUserNotExists() {
        final long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

//    @Test
//    void findByEmail_shouldFindUserByEmail_ifExists() {
//        final String email = "email";
//        AccountModel user = new AccountModel(
//                1L, "pass", "fName",
//                "lName", email, BigDecimal.TEN, true, Collections.emptySet());
//        UserDto expected = modelMapper.map(user, UserDto.class);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//
//        UserDetails actual = userService.loadUserByUsername(email);
//
//        assertEquals(expected.getEmail(), actual.getUsername());
//        assertEquals(expected.g, actual.);
//    }

//    @Test
//    void findByEmail_shouldThrowEntityNotFoundException_ifUserNotExists() {
//        final String email = "email";
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail(email));
//    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnListOfUsers_always() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserModel> page1 = (Page<UserModel>) mock(Page.class);
        UserModel user1 = new UserModel(
                1L, "email", "pass", true, "fName", "lName",
                BigDecimal.TEN, Collections.emptySet());
        UserModel user2 = new UserModel(
                2L, "com", "pass", true, "fName", "lName",
                BigDecimal.ONE, Collections.emptySet());
        List<UserDto> expected = List.of(modelMapper.map(user1, UserDto.class),
                                         modelMapper.map(user2, UserDto.class));
        when(page1.getContent()).thenReturn(List.of(user1, user2));
        when(userRepository.findAll(pageable)).thenReturn(page1);

        List<UserDto> actual = userService.findAll(pageable);

        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void existsWithEmail_shouldReturnTrue_ifExistsWithEmail() {
        final String email = "email";
        final boolean expected = true;
        when(userRepository.existsByEmail(email)).thenReturn(expected);

        boolean actual = userService.existsWithEmail(email);

        assertEquals(expected, actual);
    }

    @Test
    void existsWithEmail_shouldReturnFalse_ifNotExistsWithEmail() {
        final String email = "email";
        final boolean expected = false;
        when(userRepository.existsByEmail(email)).thenReturn(expected);

        boolean actual = userService.existsWithEmail(email);

        assertEquals(expected, actual);
    }

}