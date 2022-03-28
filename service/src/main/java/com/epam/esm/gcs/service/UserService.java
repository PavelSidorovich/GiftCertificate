package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDto findById(long id);

    List<UserDto> findAll(Pageable pageable);

    UserDto signUp(SignUpUserDto signUpDto);

    boolean existsWithEmail(String email);

}
