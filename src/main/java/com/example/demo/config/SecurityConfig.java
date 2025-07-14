package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withUsername("admin1").password("{noop}admin1pass").roles("ADMIN").build(),
				User.withUsername("admin2").password("{noop}admin2pass").roles("ADMIN").build(),
				User.withUsername("user1").password("{noop}user1pass").roles("USER").build(),
				User.withUsername("user2").password("{noop}user2pass").roles("USER").build(),
				User.withUsername("user3").password("{noop}user3pass").roles("USER").build());
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/cars/**").permitAll()
				.requestMatchers("/motorcycles/**").hasRole("USER")
				.requestMatchers("/trucks/**").hasRole("ADMIN")
				.anyRequest().authenticated())
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}
}
