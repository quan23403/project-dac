package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.UserRepository;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.request.UpdateUserRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public User updateUser(UpdateUserRequest request) throws IdInvalidException {
        User userInDb = this.getUserFromSecurityContext();

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
        return this.userRepository.save(userInDb);
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
    public List<Long> getAnkenListFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                Map<String, Object> userMap = jwt.getClaim("user");
                // Truy xuất claim roles
                return (List<Long>) userMap.get("ankenListId");
            }
        }
        return Collections.emptyList();  // Hoặc xử lý trường hợp không có roles
    }

    public User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                Map<String, Object> userMap = jwt.getClaim("user");
                Optional<User> user = this.userRepository.findById((Long) userMap.get("id"));
                return user.orElse(null);
            }
        }
        return null;
    }
}
