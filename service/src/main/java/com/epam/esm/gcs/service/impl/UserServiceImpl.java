package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.repository.column.UserColumn;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.Limiter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Finds user by id
     *
     * @param id user id
     * @return user
     * @throws EntityNotFoundException if user with id not exists
     */
    @Override
    public UserDto findById(long id) {
        UserModel user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        UserDto.class, UserColumn.ID.getColumnName(), id
                )
        );
        userRepository.clear();
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Finds user by email
     *
     * @param email user email
     * @return user
     * @throws EntityNotFoundException if user with such email not exists
     */
    @Override
    public UserDto findByEmail(String email) {
        UserModel user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(
                        UserDto.class, UserColumn.EMAIL.getColumnName(), email
                )
        );
        userRepository.clear();
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Finds all users
     * @param limiter query limiter
     * @return list of users
     */
    @Override
    public List<UserDto> findAll(Limiter limiter) {
        List<UserDto> users =
                userRepository.findAll(limiter).stream()
                              .map(user -> modelMapper.map(user, UserDto.class))
                              .collect(Collectors.toList());
        userRepository.clear();
        return users;
    }

    /**
     * Checks if user exists with provided email
     * @param email user email
     * @return true if exists, otherwise — false
     */
    @Override
    public boolean existsWithEmail(String email) {
        boolean exists = userRepository.existsWithEmail(email);
        userRepository.clear();
        return exists;
    }

}
