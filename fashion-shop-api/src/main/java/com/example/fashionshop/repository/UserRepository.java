package com.example.fashionshop.repository;

import com.example.fashionshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirebaseUid(String firebaseUid);

    Optional<User> findByEmail(String email);

    boolean existsByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);

    long countByRoleIgnoreCaseAndStatusIgnoreCase(String role, String status);

    @Query("""
            SELECT u
            FROM User u
            WHERE (:status IS NULL OR :status = '' OR UPPER(u.status) = UPPER(:status))
              AND (:role IS NULL OR :role = '' OR UPPER(u.role) = UPPER(:role))
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(COALESCE(u.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(u.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR CONCAT('', u.id) LIKE CONCAT('%', :keyword, '%')
                  )
            ORDER BY u.id DESC
            """)
    List<User> searchAdminUsers(String keyword, String role, String status);
}
