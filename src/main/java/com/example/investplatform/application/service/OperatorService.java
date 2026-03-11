package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.CreateOperatorDto;
import com.example.investplatform.application.dto.OperatorResponseDto;
import com.example.investplatform.application.exception.RoleNotFoundException;
import com.example.investplatform.application.exception.UsernameAlreadyTakenException;
import com.example.investplatform.infrastructure.repository.OperatorRepository;
import com.example.investplatform.infrastructure.repository.RoleRepository;
import com.example.investplatform.infrastructure.repository.UserRepository;
import com.example.investplatform.infrastructure.security.RoleEnum;
import com.example.investplatform.model.entity.user.Operator;
import com.example.investplatform.model.entity.user.Role;
import com.example.investplatform.model.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public OperatorResponseDto create(CreateOperatorDto dto) {
        if (userRepository.findByEmailWithRoles(dto.email()).isPresent()) {
            throw new UsernameAlreadyTakenException(
                    "Пользователь с email '%s' уже существует".formatted(dto.email()));
        }

        String roleName = ROLE_PREFIX + RoleEnum.OPERATOR.name();
        Role operatorRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(
                        "Роль '%s' не найдена".formatted(roleName)));

        User user = User.builder()
                .email(dto.email())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .isEnabled(true)
                .isAccountNonLocked(true)
                .is2faEnabled(false)
                .roles(Set.of(operatorRole))
                .build();
        userRepository.save(user);

        Operator operator = Operator.builder()
                .user(user)
                .lastName(dto.lastName())
                .firstName(dto.firstName())
                .patronymic(dto.patronymic())
                .build();
        operatorRepository.save(operator);

        return new OperatorResponseDto(
                operator.getId(),
                user.getEmail(),
                operator.getLastName(),
                operator.getFirstName(),
                operator.getPatronymic()
        );
    }
}
