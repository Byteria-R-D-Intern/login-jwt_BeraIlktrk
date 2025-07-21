package com.staj.login_jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EntityScan("com.staj.login_jwt.domain.entity")
@EnableJpaRepositories("com.staj.login_jwt.infrastructure.repository")
public class LoginJwtApplication {

	public static void main(String[] args) {
		// Geçici olarak 123456 şifresi için BCrypt hash üret
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("123456 için BCrypt hash: " + encoder.encode("123456"));
		SpringApplication.run(LoginJwtApplication.class, args);
	}

	public static class HashGenerator {
		public static void main(String[] args) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String rawPassword = "123456";
			String encodedPassword = encoder.encode(rawPassword);
			System.out.println(encodedPassword);
		}
	}

}
