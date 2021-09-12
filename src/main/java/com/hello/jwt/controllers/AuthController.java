package com.hello.jwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.hello.jwt.models.Role;
import com.hello.jwt.models.User;
import com.hello.jwt.payload.request.LoginRequest;
import com.hello.jwt.payload.response.JWTResponse;
import com.hello.jwt.payload.response.MessageResponse;
import com.hello.jwt.repository.RoleRepository;
import com.hello.jwt.repository.UserRepository;
import com.hello.jwt.security.jwt.JwtUtils;
import com.hello.jwt.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.hello.jwt.models.EnumRole;
import com.hello.jwt.payload.request.SignupRequest;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	AuthenticationManager authenticationManager;
	UserRepository userRepository;
	RoleRepository roleRepository;
	PasswordEncoder encoder;
	JwtUtils jwtUtils;

	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Autowired
	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Autowired
	public void setJwtUtils(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		logger.info("Authenticating user:{}" , loginRequest.getUsername());

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
						                                loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JWTResponse(jwt,
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		User user = new User(signUpRequest.getUsername(),
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();

		Set<Role> roles = new HashSet<>();
		if (strRoles == null) {
			checkAndAddRole(roles, EnumRole.USER);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					checkAndAddRole(roles, EnumRole.ADMIN);
					break;
				case "mod":
					checkAndAddRole(roles, EnumRole.MODERATOR);
					break;
				default:
					checkAndAddRole(roles, EnumRole.USER);
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	private void checkAndAddRole(Set<Role> roles, EnumRole roleToCheck) {
		Role adminRole = roleRepository.findByName(roleToCheck)
				.orElseThrow(() -> new RuntimeException("Error: Cannot find role:" + roleToCheck));
		roles.add(adminRole);
	}
}
