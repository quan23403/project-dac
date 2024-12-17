package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.request.UpdateUserRequest;
import com.example.ProjectDAC.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello";
    }

    @PostMapping("/users")
    public ResponseEntity<ResUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email already exists" );
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        ResUserDTO newUser = this.userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/users")
    public ResponseEntity<ResUserDTO> update(@RequestBody UpdateUserRequest request) throws IdInvalidException {
        ResUserDTO res = this.userService.updateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

//    @GetMapping("/current-user")
//    public List<Long> getRolesFromSecurityContext() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof Jwt) {
//                Jwt jwt = (Jwt) principal;
//                Map<String, Object> userMap = jwt.getClaim("user");
//                // Truy xuất claim roles
//                return (List<Long>) userMap.get("ankenListId");
//            }
//        }
//        return Collections.emptyList();  // Hoặc xử lý trường hợp không có roles
//    }
}
