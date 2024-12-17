package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.ResLoginDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.request.UpdateUserRequest;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.JwtUtils;
import com.example.ProjectDAC.util.constant.ERole;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello";
    }

    @PostMapping("/user")
    public ResponseEntity<ResUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email already exists" );
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        HashSet<String> roles = new HashSet<>();
        roles.add(ERole.USER.name());
        user.setRoles(roles);
        ResUserDTO newUser = this.userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

//    @PutMapping("/user")
//    public ResponseEntity<ResLoginDTO> update(@RequestBody UpdateUserRequest request) throws IdInvalidException, JsonProcessingException {
//        User user = this.userService.updateUser(request, id);
//        String token = this.jwtUtils.generateToken(user);
//        ResLoginDTO resLoginDTO = new ResLoginDTO();
//        resLoginDTO.setId(user.getId());
//        resLoginDTO.setFirstName(user.getFirstName());
//        resLoginDTO.setLastName(user.getLastName());
//        resLoginDTO.setAnkenList(user.getAnkenList());
//        resLoginDTO.setToken(token);
//        return ResponseEntity.status(HttpStatus.OK).body(resLoginDTO);
//    }

    @PutMapping("/user/{id}")
    public ResponseEntity<ResLoginDTO> updateUser(@RequestBody UpdateUserRequest request, @PathVariable long id) throws IdInvalidException, JsonProcessingException {
        User user = this.userService.updateUser(request, id);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setId(user.getId());
        resLoginDTO.setFirstName(user.getFirstName());
        resLoginDTO.setLastName(user.getLastName());
        resLoginDTO.setAnkenList(user.getAnkenList());
        resLoginDTO.setRoles(user.getRoles());
        return ResponseEntity.status(HttpStatus.OK).body(resLoginDTO);
    }

    @GetMapping("/current-user")
    public ResUserDTO getRolesFromSecurityContext() throws IdInvalidException {
        User user = this.userService.getUserFromSecurityContext();
        return this.userService.convertUserToResUserDTO(user);
    }

    @GetMapping("/user")
    public List<ResUserDTO> getAllUser() {
        return this.userService.getAllUser();
    }
}
