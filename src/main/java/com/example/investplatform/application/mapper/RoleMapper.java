package com.example.investplatform.application.mapper;

import com.example.investplatform.application.dto.RoleDto;
import com.example.investplatform.model.entity.user.Role;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {
        return new RoleDto(
                role.getId(),
                role.getName()
        );
    }

    public Collection<RoleDto> toDtos(Collection<Role> roles) {
        return roles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
