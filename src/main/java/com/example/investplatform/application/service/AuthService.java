package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.LoginRequestDto;
import com.example.investplatform.application.dto.LoginResponseDto;
import com.example.investplatform.application.exception.UserNotFoundException;
import com.example.investplatform.application.mapper.AccessTokenInputMapper;
import com.example.investplatform.infrastructure.repository.UserRepository;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import com.example.investplatform.infrastructure.security.jwt.JwtTokenFactory;
import com.example.investplatform.infrastructure.security.jwt.dto.AccessTokenInput;
import com.example.investplatform.infrastructure.security.jwt.parser.RefreshTokenParser;
import com.example.investplatform.model.entity.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenFactory jwtTokenFactory;
    private final RefreshTokenParser refreshTokenParser;
    private final UserRepository userRepository;
    private final AccessTokenInputMapper accessTokenInputMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenFactory jwtTokenFactory,
                       RefreshTokenParser refreshTokenParser,
                       UserRepository userRepository,
                       AccessTokenInputMapper accessTokenInputMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenFactory = jwtTokenFactory;
        this.refreshTokenParser = refreshTokenParser;
        this.userRepository = userRepository;
        this.accessTokenInputMapper = accessTokenInputMapper;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        AccessTokenInput accessTokenInput = accessTokenInputMapper.from(userDetails);

        return new LoginResponseDto(
                jwtTokenFactory.generateAccessToken(accessTokenInput),
                jwtTokenFactory.generateRefreshToken(accessTokenInput.userId())
        );
    }

    public LoginResponseDto refresh(String refreshToken) {
        Long userId = refreshTokenParser.getUserId(refreshToken);
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("В токен зашит id %s пользователя, которого не существует", userId))
                );

        AccessTokenInput accessTokenInput = accessTokenInputMapper.from(user);

        return new LoginResponseDto(
                jwtTokenFactory.generateAccessToken(accessTokenInput),
                jwtTokenFactory.generateRefreshToken(accessTokenInput.userId())
        );
    }
}
