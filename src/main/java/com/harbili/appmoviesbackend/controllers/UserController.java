package com.harbili.appmoviesbackend.controllers;

import com.harbili.appmoviesbackend.dto.UserDTO;
import com.harbili.appmoviesbackend.entities.User;
import com.harbili.appmoviesbackend.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public Object createUser(@RequestBody User user) {
        user.setId(null);
        UserDTO createdUser = userService.createUser(user);
        if (createdUser == null) {
            return Map.of("message", "Email already exists");
        }
        return createdUser;
    }

    @PutMapping("/{id}")
    public Object updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        UserDTO user = userService.updateUser(id, updatedUser);
        if (user != null) {
            return user;
        } else {
            return Map.of("message", "User not found");
        }
    }

    @DeleteMapping("/{id}")
    public Object deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Map.of("message", "User deleted successfully");
        } else {
            return Map.of("message", "User not found");
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        UserDTO user = userService.login(username, password);
        if (user != null) {
            return user;
        } else {
            return Map.of("message", "Invalid username or password");
        }
    }
}
