package com.epam.esm.gcs.config;

import com.epam.esm.gcs.filter.JwtRequestFilter;
import com.epam.esm.gcs.model.RoleName;
import com.epam.esm.gcs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests().antMatchers("/auth/**").permitAll()
            .antMatchers(HttpMethod.POST, "/certificates").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/certificates/**").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.PATCH, "/certificates/**").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers("/stats/**").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, "/tags/**").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.POST, "/tags/**").hasAuthority(RoleName.ROLE_ADMIN.name())
            .antMatchers(HttpMethod.POST, "/users/**").hasAuthority(RoleName.ROLE_USER.name())
            .antMatchers(HttpMethod.GET, "/certificates/**").permitAll()
            .anyRequest().authenticated()
            .and().logout().permitAll()
            .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationManager(authManager);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder);
    }

}
