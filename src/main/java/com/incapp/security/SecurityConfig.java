package com.incapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	BCryptPasswordEncoder getpasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
				HttpSession session = request.getSession(false);
				String role = (session != null) ? (String) session.getAttribute("oauth2_role") : null;
				if (role != null && role.equals("doctor")) {
					response.sendRedirect("/doctor/oauth2success");
				} else {
					response.sendRedirect("/user/oauth2success");
				}
				if (session != null) session.removeAttribute("oauth2_role");
			}
		};
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .anyRequest().permitAll() // ✅ Permit ALL URLs
	        )
            // Google login
            .oauth2Login(oauth -> oauth
                .loginPage("/login-signup")
                .successHandler(oAuth2AuthenticationSuccessHandler())
            )
            // No Spring-managed form login!
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login-signup")
                .permitAll()
            );

        return http.build();
    }
}
