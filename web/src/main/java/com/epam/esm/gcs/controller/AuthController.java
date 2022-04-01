package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.auth.JwtTokenService;
import com.epam.esm.gcs.dto.LoginUserDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginUserDto loginDto) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(loginDto.getEmail());
        return jwtTokenService.generateToken(userDetails);
    }

    @PostMapping("/signup")
    public UserDto signUp(@Valid @RequestBody SignUpUserDto signUpDto) {
        return userService.signUp(signUpDto);
    }

}
