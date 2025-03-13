package com.example.wedding.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.wedding.dto.UserDto;
import com.example.wedding.model.User;
import com.example.wedding.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    public User findByEmail(String email) {
        return repo.findByEmail(email);
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
}
