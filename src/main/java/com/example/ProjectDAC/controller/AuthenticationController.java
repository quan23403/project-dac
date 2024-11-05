package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.AuthenticationRequest;
import com.example.ProjectDAC.response.ResCreateUserDTO;
import com.example.ProjectDAC.response.ResLoginDTO;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResLoginDTO> authenticate(@RequestBody AuthenticationRequest request) {
        // Nap input gom username/pass
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        // Xac thuc nguoi dung
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtUtils.generateToken(user);
        ResLoginDTO result = new ResLoginDTO();
        result.setId(user.getId());
        result.setEmail(user.getEmail());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setToken(token);
        System.out.println("Log in success");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/register")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email da ton tai" );
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        ResCreateUserDTO newUser = this.userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
