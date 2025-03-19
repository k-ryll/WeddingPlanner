package com.example.wedding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wedding.model.Guest;
import com.example.wedding.model.User;
import com.example.wedding.repository.GuestRepository;


@Service
public class GuestService {
	@Autowired
	private GuestRepository repo;
	

    public List<Guest> findByUser(User user) {
        return repo.findByAddedBy(user);
    }
	
	public String save(Guest guest) {
		repo.save(guest);
		return "redirect:/home";
		
	}
}
