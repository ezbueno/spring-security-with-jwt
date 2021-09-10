package com.buenoezandro.userservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buenoezandro.userservice.model.Role;
import com.buenoezandro.userservice.model.UserModel;
import com.buenoezandro.userservice.repository.RoleRepository;
import com.buenoezandro.userservice.repository.UserRepository;
import com.buenoezandro.userservice.security.LoggedUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public void setUserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = this.userRepository.findByUsername(username);

		if (user == null) {
			log.error("User not found in the database!");
			throw new UsernameNotFoundException("User not found in the database!");
		} else {
			log.info("User found in the database: {}", username);
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

		LoggedUser loggedUser = new LoggedUser();
		loggedUser.setUser(user);
		loggedUser.setAuthorities(authorities);

		return loggedUser;
	}

	@Override
	public UserModel saveUser(UserModel user) {
		log.info("Saving new user {} to the database", user.getUsername());
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		return this.userRepository.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		return this.roleRepository.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		log.info("Adding role {} to user {}", roleName, username);

		var user = this.userRepository.findByUsername(username);
		var role = this.roleRepository.findByName(roleName);

		user.getRoles().add(role);
	}

	@Override
	public UserModel getUser(String username) {
		log.info("Fetching user {}", username);
		return this.userRepository.findByUsername(username);
	}

	@Override
	public List<UserModel> getUsers() {
		log.info("Fetching all users");
		return this.userRepository.findAll();
	}

}