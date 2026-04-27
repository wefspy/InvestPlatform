package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.user.UserAdminListItemDto;
import com.example.investplatform.infrastructure.security.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private static final String ROLE_PREFIX = "ROLE_";

    private static final String DISPLAY_NAME_EXPR = """
            (CASE
              WHEN r.name = 'ROLE_OPERATOR'
                THEN TRIM(BOTH FROM (op.last_name || ' ' || op.first_name || COALESCE(' ' || op.patronymic, '')))
              WHEN r.name = 'ROLE_INVESTOR' AND inv.investor_type = 'INDIVIDUAL'
                THEN TRIM(BOTH FROM (ii.last_name || ' ' || ii.first_name || COALESCE(' ' || ii.patronymic, '')))
              WHEN r.name = 'ROLE_INVESTOR' AND inv.investor_type = 'PRIVATE_ENTREPRENEUR'
                THEN TRIM(BOTH FROM (ipe.last_name || ' ' || ipe.first_name || COALESCE(' ' || ipe.patronymic, '')))
              WHEN r.name = 'ROLE_INVESTOR' AND inv.investor_type = 'LEGAL_ENTITY'
                THEN ile.full_name
              WHEN r.name = 'ROLE_EMITENT' AND em.emitent_type = 'PRIVATE_ENTREPRENEUR'
                THEN TRIM(BOTH FROM (epe.last_name || ' ' || epe.first_name || COALESCE(' ' || epe.patronymic, '')))
              WHEN r.name = 'ROLE_EMITENT' AND em.emitent_type = 'LEGAL_ENTITY'
                THEN ele.full_name
              ELSE NULL
            END)
            """;

    private static final String SUBTYPE_EXPR = """
            (CASE
              WHEN r.name = 'ROLE_INVESTOR' THEN inv.investor_type
              WHEN r.name = 'ROLE_EMITENT' THEN em.emitent_type
              ELSE NULL
            END)
            """;

    private static final String FROM_AND_JOINS = """
            FROM users u
            JOIN roles r ON r.id = u.role_id
            LEFT JOIN operators op ON op.user_id = u.id
            LEFT JOIN investors inv ON inv.user_id = u.id
            LEFT JOIN investor_individuals ii ON ii.investor_id = inv.id
            LEFT JOIN investor_private_entrepreneurs ipe ON ipe.investor_id = inv.id
            LEFT JOIN investor_legal_entities ile ON ile.investor_id = inv.id
            LEFT JOIN emitents em ON em.user_id = u.id
            LEFT JOIN emitent_private_entrepreneurs epe ON epe.emitent_id = em.id
            LEFT JOIN emitent_legal_entities ele ON ele.emitent_id = em.id
            """;

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "u.id",
            "email", "u.email",
            "role", "r.name",
            "displayName", "display_name",
            "isEnabled", "u.is_enabled",
            "createdAt", "u.created_at",
            "updatedAt", "u.updated_at"
    );

    private static final String DEFAULT_SORT_COLUMN = "u.created_at";

    private final NamedParameterJdbcTemplate jdbc;

    @Transactional(readOnly = true)
    public Page<UserAdminListItemDto> findAll(String roleFilter, String search, Pageable pageable) {
        String normalizedRole = normalizeRole(roleFilter);
        String normalizedSearch = (search == null || search.isBlank()) ? null : "%" + search.toLowerCase() + "%";

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (normalizedRole != null) {
            where.append(" AND r.name = :role ");
            params.addValue("role", normalizedRole);
        }
        if (normalizedSearch != null) {
            where.append(" AND (LOWER(u.email) LIKE :search OR LOWER(")
                    .append(DISPLAY_NAME_EXPR)
                    .append(") LIKE :search) ");
            params.addValue("search", normalizedSearch);
        }

        String countSql = "SELECT COUNT(*) " + FROM_AND_JOINS + where;
        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalCount = total == null ? 0L : total;

        if (totalCount == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        String orderBy = buildOrderBy(pageable.getSort());

        String dataSql = """
                SELECT
                  u.id AS id,
                  u.email AS email,
                  r.name AS role_name,
                  u.is_enabled AS is_enabled,
                  u.is_account_non_locked AS is_account_non_locked,
                  u.is_2fa_enabled AS is_2fa_enabled,
                  u.created_at AS created_at,
                  u.updated_at AS updated_at,
                """
                + DISPLAY_NAME_EXPR + " AS display_name,\n"
                + SUBTYPE_EXPR + " AS subtype "
                + FROM_AND_JOINS
                + where
                + orderBy
                + " LIMIT :limit OFFSET :offset ";

        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());

        List<UserAdminListItemDto> items = jdbc.query(dataSql, params, (rs, rowNum) -> {
            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            return new UserAdminListItemDto(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("role_name"),
                    rs.getString("display_name"),
                    rs.getString("subtype"),
                    rs.getBoolean("is_enabled"),
                    rs.getBoolean("is_account_non_locked"),
                    rs.getBoolean("is_2fa_enabled"),
                    createdAt == null ? null : createdAt.toLocalDateTime(),
                    updatedAt == null ? null : updatedAt.toLocalDateTime()
            );
        });

        return new PageImpl<>(items, pageable, totalCount);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        String upper = role.trim().toUpperCase();
        String name = upper.startsWith(ROLE_PREFIX) ? upper.substring(ROLE_PREFIX.length()) : upper;
        try {
            RoleEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Недопустимая роль: '%s'. Допустимые значения: ADMIN, OPERATOR, EMITENT, INVESTOR".formatted(role));
        }
        return ROLE_PREFIX + name;
    }

    private String buildOrderBy(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return " ORDER BY " + DEFAULT_SORT_COLUMN + " DESC ";
        }
        StringBuilder sb = new StringBuilder(" ORDER BY ");
        boolean first = true;
        for (Sort.Order order : sort) {
            String column = SORT_FIELDS.get(order.getProperty());
            if (column == null) {
                throw new IllegalArgumentException(
                        "Недопустимое поле сортировки: '%s'. Допустимые: %s"
                                .formatted(order.getProperty(), SORT_FIELDS.keySet()));
            }
            if (!first) {
                sb.append(", ");
            }
            sb.append(column).append(order.isAscending() ? " ASC" : " DESC");
            sb.append(" NULLS LAST");
            first = false;
        }
        return sb.toString();
    }
}
