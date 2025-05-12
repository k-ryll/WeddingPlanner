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
	                "/", "/index", "/user/signup", "/user/save",
	                "/user/resetpassword", "/user/reset", "/user/reset-password",
	                "/verify", "/user/login", "/user/validate",
	                "/admin/login", "/admin/validate",
	                "/styles/**", "/js/**", "/images/**",
	                "/error", "/403"
	            ).permitAll()
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .anyRequest().authenticated()
	        )
				
				 .formLogin(login -> login .loginPage("/user/login")
				 .loginProcessingUrl("/user/validate")
				 .defaultSuccessUrl("/home", true) .failureUrl("/user/login?error=true")
				 .permitAll() )
				 
	        .formLogin(login -> login
	            .loginPage("/admin/login")
	            .loginProcessingUrl("/admin/validate")
	            .defaultSuccessUrl("/admin/home", true)
	            .failureUrl("/admin/login?error=true")
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	            .logoutSuccessUrl("/user/login")
	            .invalidateHttpSession(true)
	            .clearAuthentication(true)
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	        )
	        .exceptionHandling(exception -> exception
	            .accessDeniedPage("/403")
	        );

	    return http.build();
	}


}