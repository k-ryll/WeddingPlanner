package com.example.wedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.wedding.model.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email=?1")
    User findByEmail(String email);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.password = ?1 WHERE u.email = ?2")
    void changeUserPassword(String password, String email);

}
