package com.example.wedding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wedding.model.Guest;
import com.example.wedding.model.Project;
import com.example.wedding.model.User;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
	 List<Guest> findByAddedBy(User user);
	 List<Guest> findByProjectId(Project project);
}
