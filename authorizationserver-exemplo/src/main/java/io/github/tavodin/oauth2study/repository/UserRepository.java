package io.github.tavodin.oauth2study.repository;

import io.github.tavodin.oauth2study.entities.User;
import io.github.tavodin.oauth2study.projections.UserProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    @Value("SELECT u FROM User WHERE u.email = :email")
    Optional<User> searchByEmail(@Param("email") String email);

    Optional<UserProjection> findByEmail(String email);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    Set<String> findRoleNameWithUserId(@Param("userId") Long userId);

}
