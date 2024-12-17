package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    Optional<ForgotPassword> findByOtpAndUser(int otp, User user);
    ForgotPassword findByUser(User user);
}
