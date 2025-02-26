package com.example.wedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wedding.model.Guest;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
	
}
