package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.UserDTO;
import com.harbili.appmoviesbackend.entities.User;
import com.harbili.appmoviesbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
        }
        return usersDto;
    }

    public UserDTO getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        } else {
            return null;
        }
    }

    public UserDTO createUser(User user) {
        if (user == null || emailExists(user.getEmail())) {
            return null;
        }
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public UserDTO updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            User savedUser = userRepository.save(user);
            return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        }
        return null;
    }

    private boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }
}
