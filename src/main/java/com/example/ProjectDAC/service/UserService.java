package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.UserRepository;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AnkenService ankenService;
    public UserService(UserRepository userRepository,  AnkenService ankenService) {
        this.userRepository = userRepository;
        this.ankenService = ankenService;
    }
    public ResUserDTO create(User user) throws IdInvalidException {
        User newUser = this.userRepository.save(user);
        return this.convertUserToResUserDTO(newUser);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public ResUserDTO convertUserToResUserDTO(User user) throws IdInvalidException {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setFirstName(user.getFirstName());
        resUserDTO.setLastName(user.getLastName());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUserDTO.setAnkenListId(this.getAnkenIdsFromUser(user.getId()));
        return resUserDTO;
    }

    public ResUserDTO updateUser(UpdateUserRequest request) throws IdInvalidException {
        User userInDb = this.userRepository.findByEmail(request.getEmail());

        userInDb.setFirstName(request.getFirstName());
        userInDb.setLastName(request.getLastName());

        if(request.getListAnkenId().isEmpty()) {
            userInDb.setAnkenList(null);
        }
        else {
            List<Anken> ankenList = new ArrayList<>();
            for(long id : request.getListAnkenId()) {
                Optional<Anken> anken = this.ankenService.getAnkenById(id);
                anken.ifPresent(ankenList::add);
            }
            userInDb.setAnkenList(ankenList);
        }
        User updatedUser = this.userRepository.save(userInDb);
        return this.convertUserToResUserDTO(updatedUser);
    }

    public List<Long> getAnkenIdsFromUser(long id) throws IdInvalidException {
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isEmpty()) {
            throw new IdInvalidException("User does not exist");
        }
        List<Long> ids = userOptional.get().getAnkenList().stream()
                .map(Anken::getId).toList();
        return ids;
    }
}
