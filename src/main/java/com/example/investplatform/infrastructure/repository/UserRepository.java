package com.example.investplatform.infrastructure.repository;

import com.example.investplatform.model.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithRoles(Long id);
}
