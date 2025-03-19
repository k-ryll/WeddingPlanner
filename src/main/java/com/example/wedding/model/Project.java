package com.example.wedding.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "project")
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	private Integer id;
	
	@Column(nullable = false, length = 100)
	private String projectName;
	
	@OneToOne
    @JoinColumn(name = "groom", nullable = false) 
    private User groom;
	
	@OneToOne
    @JoinColumn(name = "bride", nullable = false) 
    private User bride;
	
	@ManyToOne
    @JoinColumn(name = "organizer", nullable = true) 
    private User organizer;
	
    @Column(nullable = false)
    private LocalDateTime weddingDate;
    
    @Column(nullable = false)
    private String status;

	
}
