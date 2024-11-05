package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.repository.UserRepository;
import com.example.ProjectDAC.response.ResCreateUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public ResCreateUserDTO create(User user){
        User newUser = this.userRepository.save(user);

        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(newUser.getId());
        resCreateUserDTO.setEmail(newUser.getEmail());
        resCreateUserDTO.setFirstName(newUser.getFirstName());
        resCreateUserDTO.setLastName(newUser.getLastName());
        return resCreateUserDTO;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
