package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.*;
import com.example.investplatform.application.exception.InvalidTwoFactorCodeException;
import com.example.investplatform.application.exception.UserNotFoundException;
import com.example.investplatform.application.mapper.AccessTokenInputMapper;
import com.example.investplatform.infrastructure.repository.UserRepository;
import com.example.investplatform.infrastructure.security.UserDetailsImpl;
import com.example.investplatform.infrastructure.security.jwt.JwtTokenFactory;
import com.example.investplatform.infrastructure.security.jwt.dto.AccessTokenInput;
import com.example.investplatform.infrastructure.security.jwt.parser.RefreshTokenParser;
import com.example.investplatform.infrastructure.security.jwt.parser.TwoFactorTokenParser;
import com.example.investplatform.model.entity.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenFactory jwtTokenFactory;
    private final RefreshTokenParser refreshTokenParser;
    private final TwoFactorTokenParser twoFactorTokenParser;
    private final UserRepository userRepository;
    private final AccessTokenInputMapper accessTokenInputMapper;
    private final UserDisplayNameResolver displayNameResolver;
    private final TotpService totpService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenFactory jwtTokenFactory,
                       RefreshTokenParser refreshTokenParser,
                       TwoFactorTokenParser twoFactorTokenParser,
                       UserRepository userRepository,
                       AccessTokenInputMapper accessTokenInputMapper,
                       UserDisplayNameResolver displayNameResolver,
                       TotpService totpService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenFactory = jwtTokenFactory;
        this.refreshTokenParser = refreshTokenParser;
        this.twoFactorTokenParser = twoFactorTokenParser;
        this.userRepository = userRepository;
        this.accessTokenInputMapper = accessTokenInputMapper;
        this.displayNameResolver = displayNameResolver;
        this.totpService = totpService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        AccessTokenInput accessTokenInput = accessTokenInputMapper.from(userDetails);
        String displayName = displayNameResolver.resolve(accessTokenInput.userId(), accessTokenInput.roles());

        if (userDetails.is2faEnabled() && userDetails.getTwoFaSecretHash() != null) {
            return new LoginResponseDto(
                    accessTokenInput.userId(),
                    accessTokenInput.roles(),
                    displayName,
                    null,
                    null,
                    true,
                    jwtTokenFactory.generateTwoFactorToken(accessTokenInput.userId())
            );
        }

        return new LoginResponseDto(
                accessTokenInput.userId(),
                accessTokenInput.roles(),
                displayName,
                jwtTokenFactory.generateAccessToken(accessTokenInput),
                jwtTokenFactory.generateRefreshToken(accessTokenInput.userId()),
                false,
                null
        );
    }

    public LoginResponseDto verifyTwoFactor(TwoFactorVerifyLoginDto request) {
        Long userId = twoFactorTokenParser.getUserId(request.twoFactorToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с ID %d не найден".formatted(userId)));

        if (!totpService.verifyCode(user.getTwoFaSecretHash(), request.code())) {
            throw new InvalidTwoFactorCodeException("Неверный код 2FA");
        }

        AccessTokenInput accessTokenInput = accessTokenInputMapper.from(user);
        String displayName = displayNameResolver.resolve(accessTokenInput.userId(), accessTokenInput.roles());

        return new LoginResponseDto(
                accessTokenInput.userId(),
                accessTokenInput.roles(),
                displayName,
                jwtTokenFactory.generateAccessToken(accessTokenInput),
                jwtTokenFactory.generateRefreshToken(accessTokenInput.userId()),
                false,
                null
        );
    }

    @Transactional
    public TwoFactorSetupResponseDto setup2fa(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с ID %d не найден".formatted(userId)));

        if (user.getIs2faEnabled()) {
            throw new IllegalStateException("2FA уже включена. Сначала отключите текущую.");
        }

        String secret = totpService.generateSecret();
        user.setTwoFaSecretHash(secret);
        userRepository.save(user);

        String qrUri = totpService.buildQrUri(secret, user.getEmail());
        return new TwoFactorSetupResponseDto(secret, qrUri);
    }

    @Transactional
    public void confirm2fa(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с ID %d не найден".formatted(userId)));

        if (user.getTwoFaSecretHash() == null) {
            throw new IllegalStateException("Сначала вызовите /api/auth/2fa/setup");
        }

        if (user.getIs2faEnabled()) {
            throw new IllegalStateException("2FA уже включена");
        }

        if (!totpService.verifyCode(user.getTwoFaSecretHash(), code)) {
            throw new InvalidTwoFactorCodeException("Неверный код 2FA. Проверьте настройки приложения-аутентификатора.");
        }

        user.setIs2faEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void disable2fa(Long userId, TwoFactorDisableDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь с ID %d не найден".formatted(userId)));

        if (!user.getIs2faEnabled()) {
            throw new IllegalStateException("2FA не включена");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        if (!totpService.verifyCode(user.getTwoFaSecretHash(), request.code())) {
            throw new InvalidTwoFactorCodeException("Неверный код 2FA");
        }

        user.setIs2faEnabled(false);
        user.setTwoFaSecretHash(null);
        userRepository.save(user);
    }

    public LoginResponseDto refresh(String refreshToken) {
        Long userId = refreshTokenParser.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("В токен зашит id %s пользователя, которого не существует", userId))
                );

        AccessTokenInput accessTokenInput = accessTokenInputMapper.from(user);
        String displayName = displayNameResolver.resolve(accessTokenInput.userId(), accessTokenInput.roles());

        return new LoginResponseDto(
                accessTokenInput.userId(),
                accessTokenInput.roles(),
                displayName,
                jwtTokenFactory.generateAccessToken(accessTokenInput),
                jwtTokenFactory.generateRefreshToken(accessTokenInput.userId()),
                false,
                null
        );
    }
}
