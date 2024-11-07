package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.repository.UserRepository;
import com.example.ProjectDAC.response.ResLoginDTO;
import com.example.ProjectDAC.response.ResUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public ResUserDTO create(User user){
        User newUser = this.userRepository.save(user);

        return this.convertUserToResLoginDTO(newUser);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public ResUserDTO convertUserToResLoginDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setFirstName(user.getFirstName());
        resUserDTO.setLastName(user.getLastName());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }
}
