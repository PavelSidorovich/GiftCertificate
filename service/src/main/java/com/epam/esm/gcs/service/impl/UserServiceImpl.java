package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.AccountRoleDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.DuplicatePropertyException;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.PasswordsAreNotEqualException;
import com.epam.esm.gcs.model.AccountModel;
import com.epam.esm.gcs.model.AccountModel_;
import com.epam.esm.gcs.model.AccountRoleModel;
import com.epam.esm.gcs.model.CustomUserDetailsImpl;
import com.epam.esm.gcs.model.RoleName;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.AccountRepository;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.service.AccountRoleService;
import com.epam.esm.gcs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleService accountRoleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto signUp(SignUpUserDto signUpDto) {
        if (accountRepository.existsByEmail(signUpDto.getEmail())) {
            throw new DuplicatePropertyException(UserDto.class, AccountModel_.EMAIL, signUpDto.getEmail());
        }
        if (!signUpDto.getPassword().equals(signUpDto.getPasswordRepeat())) {
            throw new PasswordsAreNotEqualException();
        }
        UserModel userModel = modelMapper.map(signUpDto, UserModel.class);
        AccountRoleDto role = accountRoleService.findByName(RoleName.ROLE_USER.name());
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userModel.setRoles(Set.of(modelMapper.map(role, AccountRoleModel.class)));

        return modelMapper.map(userRepository.save(userModel), UserDto.class);
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
        AccountModel user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        UserDto.class, AccountModel_.ID, id
                )
        );
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        final AccountModel accountModel = accountRepository
                .findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("invalid credentials"));
        return new CustomUserDetailsImpl(accountModel);
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

}
