package com.buenoezandro.userservice.service;

import java.util.List;

import com.buenoezandro.userservice.model.Role;
import com.buenoezandro.userservice.model.UserModel;

public interface UserService {
	UserModel saveUser(UserModel user);

	Role saveRole(Role role);

	void addRoleToUser(String username, String roleName);

	UserModel getUser(String username);

	List<UserModel> getUsers();
}