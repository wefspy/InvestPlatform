package com.example.investplatform.infrastructure.security;

import com.example.investplatform.infrastructure.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.investplatform.model.entity.user.User;

import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь не найден: %s", username)
                ));

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getIsEnabled(),
                user.getIsAccountNonLocked(),
                user.getIs2faEnabled(),
                user.getTwoFaSecretHash(),
                user.getVersion(),
                Set.of(new SimpleGrantedAuthority(user.getRole().getAuthority()))
        );
    }
}
