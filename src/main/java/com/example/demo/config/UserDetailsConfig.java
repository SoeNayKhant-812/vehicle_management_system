package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withUsername("admin1").password("{noop}admin1pass").roles("ADMIN").build(),
				User.withUsername("admin2").password("{noop}admin2pass").roles("ADMIN").build(),
				User.withUsername("user1").password("{noop}user1pass").roles("USER").build(),
				User.withUsername("user2").password("{noop}user2pass").roles("USER").build(),
				User.withUsername("user3").password("{noop}user3pass").roles("USER").build(),
				User.withUsername("staff1").password("{noop}staff1pass").roles("STAFF").build());
	}
}
