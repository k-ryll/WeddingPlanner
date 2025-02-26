package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wedding.model.Guest;
import com.example.wedding.repository.GuestRepository;


@Service
public class GuestService {
	@Autowired
	private GuestRepository repo;
	
	public String save(Guest guest) {
		repo.save(guest);
		return "redirect:/home";
		
	}
}
