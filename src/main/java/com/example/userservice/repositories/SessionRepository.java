package com.example.userservice.repositories;

import com.example.userservice.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByTokenAndUser_id(String token, Long userId);
}
