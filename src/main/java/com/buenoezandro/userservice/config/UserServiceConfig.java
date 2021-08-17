package com.buenoezandro.userservice.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.buenoezandro.userservice.model.Role;
import com.buenoezandro.userservice.model.UserModel;
import com.buenoezandro.userservice.service.UserService;

@Configuration
public class UserServiceConfig {

	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_MANAGER = "ROLE_MANAGER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

	private static final String NAME_USER1 = "John Travolta";
	private static final String USERNAME_USER1 = "john";
	private static final String PASSWORD_USER1 = "1234";

	private static final String NAME_USER2 = "Will Smith";
	private static final String USERNAME_USER2 = "will";
	private static final String PASSWORD_USER2 = "1234";

	private static final String NAME_USER3 = "Jim Carrey";
	private static final String USERNAME_USER3 = "jim";
	private static final String PASSWORD_USER3 = "1234";

	private static final String NAME_USER4 = "Arnold Schwarzenegger";
	private static final String USERNAME_USER4 = "arnold";
	private static final String PASSWORD_USER4 = "1234";
	
	private UserService userService;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Bean
	public void insertDataForTesting() {
		this.userService.saveRole(new Role(null, ROLE_USER));
		this.userService.saveRole(new Role(null, ROLE_MANAGER));
		this.userService.saveRole(new Role(null, ROLE_ADMIN));
		this.userService.saveRole(new Role(null, ROLE_SUPER_ADMIN));

		this.userService.saveUser(new UserModel(null, NAME_USER1, USERNAME_USER1, PASSWORD_USER1, new ArrayList<>()));
		this.userService.saveUser(new UserModel(null, NAME_USER2, USERNAME_USER2, PASSWORD_USER2, new ArrayList<>()));
		this.userService.saveUser(new UserModel(null, NAME_USER3, USERNAME_USER3, PASSWORD_USER3, new ArrayList<>()));
		this.userService.saveUser(new UserModel(null, NAME_USER4, USERNAME_USER4, PASSWORD_USER4, new ArrayList<>()));

		this.userService.addRoleToUser(USERNAME_USER1, ROLE_USER);
		this.userService.addRoleToUser(USERNAME_USER1, ROLE_MANAGER);
		this.userService.addRoleToUser(USERNAME_USER2, ROLE_MANAGER);
		this.userService.addRoleToUser(USERNAME_USER3, ROLE_ADMIN);
		this.userService.addRoleToUser(USERNAME_USER4, ROLE_SUPER_ADMIN);
		this.userService.addRoleToUser(USERNAME_USER4, ROLE_ADMIN);
		this.userService.addRoleToUser(USERNAME_USER4, ROLE_USER);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}