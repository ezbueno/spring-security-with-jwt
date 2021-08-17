package com.buenoezandro.userservice.api;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.buenoezandro.userservice.model.Role;
import com.buenoezandro.userservice.model.UserModel;
import com.buenoezandro.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserModel>> getUsers() {
		List<UserModel> users = this.userService.getUsers();
		return ResponseEntity.ok().body(users);
	}

	@PostMapping(value = "/user/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserModel> saveUser(@RequestBody UserModel user) {
		var savedUser = this.userService.saveUser(user);
		var uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/api/user/save").toUriString());
		return ResponseEntity.created(uri).body(savedUser);
	}

	@PostMapping(value = "/role/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<Role> saveRole(@RequestBody Role role) {
		var savedRole = this.userService.saveRole(role);
		var uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(savedRole);
	}
	
	@PostMapping(value = "/role/addtouser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
		this.userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping(value = "/token/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refreshToken = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);

				String username = decodedJWT.getSubject();
				UserModel user = this.userService.getUser(username);
			
				
				String accessToken = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshToken);

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);

			} catch (Exception e) {
				response.setHeader("error", e.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());

				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token is missing!");
		}
	}
	
}

@Data
class RoleToUserForm {
	private String username;
	private String roleName;
}