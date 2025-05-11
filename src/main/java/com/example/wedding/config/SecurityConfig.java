package com.example.wedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) 
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/user/signup", "/index", "/user/save",
	                "/user/resetpassword", "/user/reset", "/user/reset-password",
	                "/verify", "/user/login", "/home", "/guests", "/guests/**", "/user/validate", "/project/**", 
	                "/admin/login", "/admin/**","/project/**","/guest/**", "/rsvp-success", "/styles/**", "/js/**", "/images/**",
	                "/planning", "/budget/add", "/budget/category/add", "/task/add", "/itinerary/add", 
	                "/task/*/send-email", "/itinerary/send-email", "/seatplan", "/vendors"
	            ).permitAll()
	            
	            .anyRequest().authenticated()
	        )
				
				 .formLogin(login -> login .loginPage("/user/login")
				 .defaultSuccessUrl("/home", true) .failureUrl("/user/login?error=true")
				 .permitAll() )
				 
	        .logout(logout -> logout
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	            .logoutSuccessUrl("/user/login")
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	        )
	        .exceptionHandling(exception -> exception
	            .accessDeniedPage("/403")
	        );

	    return http.build();
	}


}