package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserServiceImpl userService;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImplTest(@Mock UserRepository userRepository) {
        this.modelMapper = new ModelMapper();
        this.userRepository = userRepository;
        this.userService = new UserServiceImpl(userRepository, modelMapper);
    }

    @Test
    void findById_shouldFindUserById_ifExists() {
        final long userId = 1L;
        UserModel user = new UserModel(userId, "fName", "lName", "email", BigDecimal.TEN);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actual = userService.findById(1L);

        assertEquals(expected, actual);
        verify(userRepository).clear();
    }

    @Test
    void findById_shouldThrowEntityNotFoundException_ifUserNotExists() {
        final long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository, times(0)).clear();
    }

    @Test
    void findByEmail_shouldFindUserByEmail_ifExists() {
        final String email = "email";
        UserModel user = new UserModel(1L, "fName", "lName", email, BigDecimal.TEN);
        UserDto expected = modelMapper.map(user, UserDto.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto actual = userService.findByEmail(email);

        assertEquals(expected, actual);
        verify(userRepository).clear();
    }

    @Test
    void findByEmail_shouldThrowEntityNotFoundException_ifUserNotExists() {
        final String email = "email";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail(email));
        verify(userRepository, times(0)).clear();
    }

    @Test
    void findAll_shouldReturnListOfUsers_always() {
        QueryLimiter limiter = new QueryLimiter(10, 0);
        UserModel user1 = new UserModel(1L, "fName", "lName", "email", BigDecimal.TEN);
        UserModel user2 = new UserModel(2L, "fName", "lName", "com", BigDecimal.ONE);
        List<UserDto> expected = List.of(modelMapper.map(user1, UserDto.class),
                                         modelMapper.map(user2, UserDto.class));
        when(userRepository.findAll(limiter)).thenReturn(List.of(user1, user2));

        List<UserDto> actual = userService.findAll(limiter);

        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(userRepository).clear();
    }

    @Test
    void existsWithEmail_shouldReturnTrue_ifExistsWithEmail() {
        final String email = "email";
        final boolean expected = true;
        when(userRepository.existsWithEmail(email)).thenReturn(expected);

        boolean actual = userService.existsWithEmail(email);

        assertEquals(expected, actual);
        verify(userRepository).clear();
    }

    @Test
    void existsWithEmail_shouldReturnFalse_ifNotExistsWithEmail() {
        final String email = "email";
        final boolean expected = false;
        when(userRepository.existsWithEmail(email)).thenReturn(expected);

        boolean actual = userService.existsWithEmail(email);

        assertEquals(expected, actual);
        verify(userRepository).clear();
    }

}