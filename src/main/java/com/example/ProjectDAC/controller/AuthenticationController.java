package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.GoogleUserInfo;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.AuthenticationRequest;
import com.example.ProjectDAC.response.ErrorResponse;
import com.example.ProjectDAC.domain.dto.ResLoginDTO;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.JwtUtils;
import com.example.ProjectDAC.util.constant.EProvider;
import com.example.ProjectDAC.util.constant.ERole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;

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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoAPI;

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
            result.setAnkenList(user.getAnkenList());
            result.setRoles(user.getRoles());
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
        // roles
        HashSet<String> roles = new HashSet<>();
        roles.add(ERole.USER.name());
        user.setRoles(roles);
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

    @GetMapping("/google/login-url")
    public String getGoogleLoginUrl() {
        // Địa chỉ URL xác thực Google

        return authorizationUri
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + redirectUri
                + "&scope=email%20profile"
                + "&response_type=code";
    }

    @GetMapping("/callback")
    public ResponseEntity<Object> handleGoogleCallback( String code) {
        try {
            // Tạo HttpTransport và JsonFactory cho yêu cầu
            HttpTransport transport = new NetHttpTransport();

            // Tạo AuthorizationCodeTokenRequest với mã code nhận được từ Google
            AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(
                    transport, new GsonFactory(), new GenericUrl("https://oauth2.googleapis.com/token"), code)
                    .setClientAuthentication(new ClientParametersAuthentication(googleClientId, googleClientSecret))
                    .setRedirectUri(redirectUri);

            // Gửi yêu cầu và nhận token
            TokenResponse response = tokenRequest.execute();

            // Lấy access token và refresh token
            String accessToken = response.getAccessToken();

            RestTemplate restTemplate = new RestTemplate();

            // Tạo header chứa access token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            // Tạo HttpEntity với headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Gửi yêu cầu GET tới Google API
            ResponseEntity<String> result = restTemplate.exchange(userInfoAPI, HttpMethod.GET, entity, String.class);

            String responseBody = result.getBody();
            // Trả về thông tin người dùng dưới dạng JSON (response body)
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                GoogleUserInfo userInfo = objectMapper.readValue(responseBody, GoogleUserInfo.class);
                if(!this.userService.isEmailExist(userInfo.getEmail())) {
                    User user = new User();
                    user.setEmail(userInfo.getEmail());
                    user.setFirstName(userInfo.getGiven_name());
                    user.setLastName(userInfo.getFamily_name());
                    user.setProvider(EProvider.GOOGLE);
                    user.setPassword(this.passwordEncoder.encode("Google"));
                    this.userService.create(user);
                }
                AuthenticationRequest request = new AuthenticationRequest();
                request.setEmail(userInfo.getEmail());
                request.setPassword("Google");
                return this.authenticate(request);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Trả về null hoặc xử lý lỗi thích hợp
            }
        } catch (IOException e) {
            return null;
        }
    }
}
