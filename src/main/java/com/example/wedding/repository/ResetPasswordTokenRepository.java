package com.example.wedding.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wedding.model.ResetPasswordToken;



public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
	 Optional<ResetPasswordToken> findByToken(String token);
	    
	    List<ResetPasswordToken> findByExpiryDateBefore(LocalDateTime now);
}
