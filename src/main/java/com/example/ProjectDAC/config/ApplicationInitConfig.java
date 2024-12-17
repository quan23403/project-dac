package com.example.ProjectDAC.config;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.repository.UserRepository;
import com.example.ProjectDAC.util.constant.ERole;
import com.example.ProjectDAC.util.constant.EStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail("admin@gmail.com") == null) {
                HashSet<String> roles = new HashSet<>();
                roles.add(ERole.ADMIN.name());
                User user = new User();
                user.setEmail("admin@gmail.com");
                user.setFirstName("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setRoles(roles);
                user.setStatus(EStatus.ACTIVE);
                userRepository.save(user);
                System.out.println("Admin User has been created with default password: admin, please change it");
            }
        };
    }
}
