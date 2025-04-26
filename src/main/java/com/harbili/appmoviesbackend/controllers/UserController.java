package com.harbili.appmoviesbackend.controllers;

import com.harbili.appmoviesbackend.dto.UserDTO;
import com.harbili.appmoviesbackend.entities.User;
import com.harbili.appmoviesbackend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users
     * @return list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by ID
     * @param id the user ID
     * @return the user if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Get a user by username
     * @param username the username
     * @return the user if found
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Get a user by email
     * @param email the email
     * @return the user if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Create a new user
     * @param user the user to create
     * @return the created user
     */
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        UserDTO createdUser = userService.createUser(user);
        if (createdUser == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create user. Username or email may already be taken.");
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update a user
     * @param id the user ID
     * @param user the updated user data
     * @return the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        UserDTO updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update user. User not found or username/email already taken.");
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user
     * @param id the user ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Authenticate a user
     * @param credentials the username and password
     * @return the authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        UserDTO user = userService.authenticateUser(username, password);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        return ResponseEntity.ok(user);
    }
}