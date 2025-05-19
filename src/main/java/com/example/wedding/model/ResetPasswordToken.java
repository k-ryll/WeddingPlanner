package com.example.wedding.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ForeignKey;

@Entity
public class ResetPasswordToken {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(unique = true, nullable = false)
	    private String token;
	    
	    @OneToOne
	    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RESET_PASSWORD_TOKEN_USER"))
	    private User user;

	    
	    @Column(nullable = false)
	    private LocalDateTime expiryDate;
	    
	    public ResetPasswordToken() {}


	    public ResetPasswordToken(String token, User user) {
	        this.token = token;
	        this.user = user;
	        this.expiryDate = LocalDateTime.now().plusHours(24); 
	    }


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getToken() {
			return token;
		}


		public void setToken(String token) {
			this.token = token;
		}


		public User getUser() {
			return user;
		}


		public void setUser(User user) {
			this.user = user;
		}


		public LocalDateTime getExpiryDate() {
			return expiryDate;
		}


		public void setExpiryDate(LocalDateTime expiryDate) {
			this.expiryDate = expiryDate;
		}
		
		public boolean isExpired() {
		    return LocalDateTime.now().isAfter(this.expiryDate);
		}

}
