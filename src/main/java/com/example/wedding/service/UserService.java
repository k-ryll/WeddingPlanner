package com.example.wedding.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.wedding.model.User;
import com.example.wedding.repository.UserRepository;


@Service
public class UserService {
	@Autowired
	private UserRepository repo;
	
	public String save(User user) {
		repo.save(user);
		return "redirect:/home";
		
	}
	
	
public User findByEmail(String email) {
	return repo.findByEmail(email);
	}
}
