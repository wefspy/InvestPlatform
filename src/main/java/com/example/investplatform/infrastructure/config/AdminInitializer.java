package com.example.investplatform.infrastructure.config;

import com.example.investplatform.infrastructure.config.property.RootProperties;
import com.example.investplatform.infrastructure.repository.RoleRepository;
import com.example.investplatform.infrastructure.repository.UserRepository;
import com.example.investplatform.infrastructure.security.RoleEnum;
import com.example.investplatform.model.entity.user.Role;
import com.example.investplatform.model.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private static final String ROLE_PREFIX = "ROLE_";

    private final RootProperties rootProperties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = rootProperties.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            log.info("Администратор с email '{}' уже существует, пропуск инициализации", email);
            return;
        }

        String roleName = ROLE_PREFIX + RoleEnum.ADMIN.name();
        Role adminRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Роль '%s' не найдена в базе данных. Проверьте миграции Liquibase.".formatted(roleName)));

        User admin = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rootProperties.getPassword()))
                .isEnabled(true)
                .isAccountNonLocked(true)
                .is2faEnabled(false)
                .role(adminRole)
                .build();

        userRepository.save(admin);
        log.info("Администратор '{}' успешно создан", email);
    }
}
