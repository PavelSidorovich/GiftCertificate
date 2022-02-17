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

    @Override
    public List<UserDto> findAll(Limiter limiter) {
        List<UserDto> users =
                userRepository.findAll(limiter).stream()
                              .map(user -> modelMapper.map(user, UserDto.class))
                              .collect(Collectors.toList());
        userRepository.clear();
        return users;
    }

    @Override
    public boolean existsWithEmail(String email) {
        boolean exists = userRepository.existsWithEmail(email);
        userRepository.clear();
        return exists;
    }

}
