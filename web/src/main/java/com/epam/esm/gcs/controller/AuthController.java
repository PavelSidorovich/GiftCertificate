package com.epam.esm.gcs.controller;

import com.epam.esm.gcs.dto.LoginUserDto;
import com.epam.esm.gcs.dto.SignUpUserDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.JwtTokenUtil;
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
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public String authenticateUser(@Valid @RequestBody LoginUserDto loginDto) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(loginDto.getEmail());
        return jwtTokenUtil.generateToken(userDetails);
    }

    @PostMapping("/signup")
    public UserDto registerUser(@Valid @RequestBody SignUpUserDto signUpDto) {
        return userService.signUp(signUpDto);
    }

}
