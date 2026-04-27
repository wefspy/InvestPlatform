package com.example.investplatform.controller;

import com.example.investplatform.application.dto.ApiErrorDto;
import com.example.investplatform.application.dto.user.UserAdminListItemDto;
import com.example.investplatform.application.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUserRestController {

    private final UserAdminService userAdminService;

    @Operation(summary = "Список всех пользователей (администратор)",
            description = "Возвращает пагинированный список всех пользователей с email, ролью, ФИО (или наименованием для ЮЛ), "
                    + "подтипом учётной записи и статусными флагами. Поддерживается фильтрация по роли и поиск по email/ФИО. "
                    + "Сортировка задаётся стандартным параметром Pageable `sort` "
                    + "(например, sort=role,asc или sort=displayName,desc). "
                    + "Допустимые поля сортировки: id, email, role, displayName, isEnabled, createdAt, updatedAt. "
                    + "По умолчанию сортировка по дате создания (DESC).")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @ApiResponse(responseCode = "400", description = "Недопустимое значение фильтра или поля сортировки", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserAdminListItemDto>> getAll(
            @Parameter(description = "Фильтр по роли. Допустимо: ADMIN, OPERATOR, EMITENT, INVESTOR (с префиксом ROLE_ или без).")
            @RequestParam(required = false) String role,
            @Parameter(description = "Полнотекстовый поиск (без регистра) по email и ФИО/наименованию.")
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userAdminService.findAll(role, search, pageable));
    }
}
