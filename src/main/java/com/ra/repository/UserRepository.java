package com.ra.repository;

import com.ra.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    Users findByPhone(String phone);

    Boolean existsByPhone(String name);

    Boolean existsByEmail(String email);
    @Transactional
    @Modifying
    @Query("update Users u set u.password = :newPass where u.userId = :userId")
    void updatePasswordByUserId(@Param("newPass") String newPass, @Param("userId") long id);
}

