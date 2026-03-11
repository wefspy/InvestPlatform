package com.example.investplatform.infrastructure.security;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final boolean isEnabled;
    private final boolean isAccountNonLocked;
    private final boolean is2faEnabled;
    private final String twoFaSecretHash;
    private final Long version;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String email,
                           String passwordHash,
                           boolean isEnabled,
                           boolean isAccountNonLocked,
                           boolean is2faEnabled,
                           String twoFaSecretHash,
                           Long version,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isEnabled = isEnabled;
        this.isAccountNonLocked = isAccountNonLocked;
        this.is2faEnabled = is2faEnabled;
        this.twoFaSecretHash = twoFaSecretHash;
        this.version = version;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.immutable(authorities);
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
