package com.example.wedding.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;


import com.example.wedding.model.User;
import com.example.wedding.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public List<User> getBrides() {
        return repo.findAll()
            .stream()
            .filter(user -> user.getRole().equalsIgnoreCase("BRIDE"))
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public List<User> getGrooms() {
        return repo.findAll()
            .stream()
            .filter(user -> user.getRole().equalsIgnoreCase("GROOM"))
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public List<User> getOrganizers() {
        return repo.findAll()
            .stream()
            .filter(user -> user.getRole().equalsIgnoreCase("ORGANIZER"))
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public String save(User user) throws DuplicateEmailException {
        if (repo.findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException("An account with this email already exists.");
        }
        String salt = BCrypt.gensalt(10);
        String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
        user.setPassword(hashedPassword);
        repo.save(user);
        
        
        
        return "redirect:/home";
    }
    
    

    @Transactional
    public String savePassword(User user, String password) {
        String salt = BCrypt.gensalt(10);
        String hashedPassword = BCrypt.hashpw(password, salt);
        String email = user.getEmail();
        repo.changeUserPassword(hashedPassword, email);
        System.out.print("Password Changed succesfully!");
        return "redirect:/home";
    }

    
}


