package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.AccountRoleDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.BadCredentialsException;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.PasswordsAreNotEqualException;
import com.epam.esm.gcs.model.AccountModel;
import com.epam.esm.gcs.model.AccountRoleModel;
import com.epam.esm.gcs.model.RoleName;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void signUp_shouldReturnCreatedUser_whenAllCredentialsAreValid() {
        final String email = "email@mail.ru";
        final AccountRoleDto roleDto = new AccountRoleDto(1L, "ROLE_USER");
        final AccountRoleModel roleModel = new AccountRoleModel(1L, "ROLE_USER");
        final String password = "pass";
        final String encodedPassword = "$2encoded";
        final String firstName = "fName";
        final String lastName = "lName";
        final SignUpUserDto signUpDto = new SignUpUserDto(email, password, password, firstName, lastName);
        final UserModel toSave = new UserModel(
                null, email, encodedPassword, null, firstName,
                lastName, null, Set.of(roleModel)
        );
        final UserModel saved = new UserModel(
                1L, email, encodedPassword, true, firstName,
                lastName, BigDecimal.ZERO, Set.of(roleModel)
        );
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(accountRoleService.findByName(RoleName.ROLE_USER.name())).thenReturn(roleDto);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(toSave)).thenReturn(saved);

        final UserDto actual = userService.signUp(signUpDto);

        assertEquals(1L, actual.getId());
        assertEquals(email, actual.getEmail());
        assertEquals(encodedPassword, actual.getPassword());
        assertEquals(firstName, actual.getFirstName());
        assertEquals(lastName, actual.getLastName());
        assertEquals(BigDecimal.ZERO, actual.getBalance());
        assertTrue(actual.getEnabled());
    }

    @Test
    void signUp_shouldThrowDuplicatePropertyException_whenUserWithSuchEmailAlreadyExists() {
        final String email = "email@mail.ru";
        final SignUpUserDto signUpDto = new SignUpUserDto(email, "pass", "pass", "fName", "lName");
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(DuplicatePropertyException.class, () -> userService.signUp(signUpDto));
    }

    @Test
    void signUp_shouldThrowPasswordsAreNotEqualException_whenPasswordAreNotEqual() {
        final String email = "email@mail.ru";
        final SignUpUserDto signUpDto = new SignUpUserDto(email, "pass", "pass1", "fName", "lName");
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertThrows(PasswordsAreNotEqualException.class, () -> userService.signUp(signUpDto));
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

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenExistsByEmail() {
        final String email = "email";
        final Set<AccountRoleModel> roles = Set.of(
                new AccountRoleModel(1L, RoleName.ROLE_USER.name()),
                new AccountRoleModel(2L, RoleName.ROLE_ADMIN.name())
        );
        final AccountModel user = new AccountModel(1L, email, "pass", true, roles);
        final UserDto expected = modelMapper.map(user, UserDto.class);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails actual = userService.loadUserByUsername(email);

        assertEquals(expected.getEmail(), actual.getUsername());
        assertEquals(2, actual.getAuthorities().size());
        assertTrue(actual.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_USER.name())));
        assertTrue(actual.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name())));
        assertTrue(actual.isEnabled());
        assertTrue(actual.isAccountNonExpired());
        assertTrue(actual.isCredentialsNonExpired());
        assertTrue(actual.isAccountNonLocked());
    }

    @Test
    void loadUserByUsername_shouldThrowBadCredentialsException_whenUserNotExistsByEmail() {
        final String email = "email";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> userService.loadUserByUsername(email));
    }

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