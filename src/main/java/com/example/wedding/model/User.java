package com.example.wedding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table (name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(nullable = false, unique = true)
	private Integer id;
	
	@Column(nullable = false, length = 45)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String password;
	
	@Column(nullable = false, length = 45)
	private String brideFN;
	
	@Column(nullable = false, length = 45)
	private String brideLN;
	
	@Column(nullable = false, length = 45)
	private String groomFN;
	
	@Column(nullable = false, length = 45)
	private String groomLN;
	
	@Column(nullable = false, length = 45)
	private String weddingDate;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBrideFN() {
		return brideFN;
	}

	public void setBrideFN(String brideFN) {
		this.brideFN = brideFN;
	}

	public String getBrideLN() {
		return brideLN;
	}

	public void setBrideLN(String brideLN) {
		this.brideLN = brideLN;
	}

	public String getGroomFN() {
		return groomFN;
	}

	public void setGroomFN(String groomFN) {
		this.groomFN = groomFN;
	}

	public String getGroomLN() {
		return groomLN;
	}

	public void setGroomLN(String groomLN) {
		this.groomLN = groomLN;
	}

	public String getWeddingDate() {
		return weddingDate;
	}

	public void setWeddingDate(String weddingDate) {
		this.weddingDate = weddingDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	
}
