package com.mysite.aimusicsheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.aimusicsheet.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserid(String userid); // 아이디로 사용자 찾기
    boolean existsByUserid(String userid); // 아이디 중복 확인
    boolean existsByEmail(String email);  // 이메일 중복 확인
}

