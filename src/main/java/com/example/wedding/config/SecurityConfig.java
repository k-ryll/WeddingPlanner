package com.example.wedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
                    "/verify", "/user/login", "/home", "/guests", "/guests/**", 
                    "/user/validate", "/admin/login", "/admin/**","/project/**",
                    "/guest/**", "/rsvp-success", "/styles/**", "/js/**", "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/user/login")
                .defaultSuccessUrl("/home", false)  // Fixed forced redirect
                .failureHandler((request, response, exception) -> {
                    exception.printStackTrace();  // Debugging: Log failed login reason
                    response.sendRedirect("/user/login?error=true");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  // Changed from AntPathRequestMatcher
                .logoutSuccessUrl("/user/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
            )
            .requiresChannel(channel -> 
                channel.anyRequest().requiresSecure()  // Enforce HTTPS (only if necessary)
            );

        return http.build();
    }
}
