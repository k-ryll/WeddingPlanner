package com.example.wedding.dto;

public class UserDto {
	
	private String email;
	private String password;
	
	
	public UserDto(String email, String password, String fullname) {
		  super();
		  this.email = email;
		  this.password = password;
		 }
}
