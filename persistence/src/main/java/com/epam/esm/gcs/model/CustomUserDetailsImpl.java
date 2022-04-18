package com.epam.esm.gcs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class CustomUserDetailsImpl implements CustomUserDetails {

    private static final boolean ACCOUNT_NON_EXPIRED = true;
    private static final boolean ACCOUNT_NON_LOCKED = true;
    private static final boolean CREDENTIALS_NON_EXPIRED = true;

    private final AccountModel account;
    private final Collection<GrantedAuthority> authorities;

    public CustomUserDetailsImpl(AccountModel account) {
        this.account = account;
        this.authorities = mapRolesToAuthorities(account.getRoles());
    }

    @Override
    public Long getId() {
        return account.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapRolesToAuthorities(account.getRoles());
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return ACCOUNT_NON_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ACCOUNT_NON_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return CREDENTIALS_NON_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return account.getEnabled();
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(Set<AccountRoleModel> roles) {
        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
    }

}
