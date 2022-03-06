package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.config.WebAuthenticationProvider;
import com.epam.esm.gcs.dto.LoginUserDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final WebAuthenticationProvider authProvider;

    @PostMapping("/signIn")
    public void authenticateUser(@Valid @RequestBody LoginUserDto loginDto) {
        Authentication authentication = authProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @PostMapping("/signUp")
    public UserDto registerUser(@Valid @RequestBody SignUpUserDto signUpDto) {
        return userService.signUp(signUpDto);
    }

}
