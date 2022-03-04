package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.model.UserModel_;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Unsupported operation
     */
    @Override
    public UserDto create(UserDto model) {
//        if(userRepository.existsWithEmail(mo))
        throw new UnsupportedOperationException();
    }

    /**
     * Finds all users
     *
     * @param pageable pagination
     * @return list of users
     */
    @Override
    public List<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).getContent().stream()
                             .map(user -> modelMapper.map(user, UserDto.class))
                             .collect(Collectors.toList());
    }

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
                        UserDto.class, UserModel_.ID, id
                )
        );
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
                        UserDto.class, UserModel_.EMAIL, email
                )
        );
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Unsupported operation
     */
    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if user exists with provided email
     *
     * @param email user email
     * @return true if exists, otherwise — false
     */
    @Override
    public boolean existsWithEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto findTheMostActiveUser() {
        Optional<UserModel> theMostActiveUser = userRepository.findTheMostActiveUser();
        if (theMostActiveUser.isPresent()) {
            return modelMapper.map(theMostActiveUser.get(), UserDto.class);
        }
        throw new NoWidelyUsedTagException(TagDto.class);
    }

}
