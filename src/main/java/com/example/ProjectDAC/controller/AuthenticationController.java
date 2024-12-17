package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.AuthenticationRequest;
import com.example.ProjectDAC.response.ErrorResponse;
import com.example.ProjectDAC.domain.dto.ResLoginDTO;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    public AuthenticationController(UserService userService, AuthenticationManagerBuilder authenticationManagerBuilder,
                                    JwtUtils jwtUtils,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/log-in")
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            // Create the authentication token from the provided email and password
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword());

            // Authenticate the user using the authentication manager
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // Set the authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve user data from the database
            User user = userService.getUserByEmail(request.getEmail());

            // Generate JWT token
            String token = jwtUtils.generateToken(user);

            // Prepare the response DTO with user details and token
            ResLoginDTO result = new ResLoginDTO();
            result.setId(user.getId());
            result.setEmail(user.getEmail());
            result.setFirstName(user.getFirstName());
            result.setLastName(user.getLastName());
            result.setToken(token);

            // Log success information
            System.out.println("Log in success");
//            System.out.println(jwtUtils.getUsernameFromToken(token));

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (AuthenticationException e) {
            // Catch authentication failure (e.g., wrong credentials)
            System.out.println("Authentication failed: " + e.getMessage());

            // Return error response with status 401 and a failure message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", "Invalid email or password"));
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.out.println("Error during authentication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "Something went wrong"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email da ton tai" );
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        ResUserDTO newUser = this.userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/api/resource")
    public ResponseEntity<?> getResource(@RequestHeader(value = "Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Lấy phần token sau "Bearer "
            String token = authorizationHeader.substring(7);  // Loại bỏ "Bearer "
            // Tiến hành xử lý token ở đây
            System.out.println("Token: " + token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
        }

        return ResponseEntity.ok("Token hợp lệ");
    }
}
