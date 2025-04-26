package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.UserDTO;
import com.harbili.appmoviesbackend.entities.User;
import com.harbili.appmoviesbackend.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

/**
 * Service for user-related operations
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Generate a random salt
     * @return base64 encoded salt
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with a salt
     * @param password the password to hash
     * @param salt the salt to use
     * @return the hashed password
     */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Hash a password with a new random salt
     * @param password the password to hash
     * @return the salt and hashed password separated by a colon
     */
    private String hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword;
    }

    /**
     * Verify a password against a stored hash
     * @param password the password to verify
     * @param storedHash the stored hash (salt:hash)
     * @return true if the password matches
     */
    private boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String hash = parts[1];

        String calculatedHash = hashPassword(password, salt);
        return calculatedHash.equals(hash);
    }

    /**
     * Get all users
     * @return list of all users as DTOs
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Get a user by ID
     * @param id the user ID
     * @return the user DTO if found, otherwise null
     */
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * Get a user by username
     * @param username the username
     * @return the user DTO if found, otherwise null
     */
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? convertToDto(user) : null;
    }

    /**
     * Get a user by email
     * @param email the email
     * @return the user DTO if found, otherwise null
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? convertToDto(user) : null;
    }

    /**
     * Create a new user
     * @param user the user to create
     * @return the created user as DTO
     */
    public UserDTO createUser(User user) {
        // Basic validation
        if (user == null || user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            return null;
        }

        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return null;
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }

        // Hash the password
        user.setPassword(hashPassword(user.getPassword()));

        // Save the user
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * Authenticate a user
     * @param username the username
     * @param password the password
     * @return the user DTO if authentication is successful, otherwise null
     */
    public UserDTO authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null;
        }

        // Verify password
        if (verifyPassword(password, user.getPassword())) {
            return convertToDto(user);
        }

        return null;
    }

    /**
     * Update a user
     * @param id the user ID
     * @param updatedUser the updated user data
     * @return the updated user as DTO, or null if update failed
     */
    public UserDTO updateUser(Long id, User updatedUser) {
        // Find the existing user
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }

        // Basic validation
        if (updatedUser == null) {
            return null;
        }

        // Check if username is being changed and is already taken
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            User userWithSameUsername = userRepository.findByUsername(updatedUser.getUsername());
            if (userWithSameUsername != null) {
                return null;
            }
            existingUser.setUsername(updatedUser.getUsername());
        }

        // Check if email is being changed and is already taken
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            User userWithSameEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (userWithSameEmail != null) {
                return null;
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(hashPassword(updatedUser.getPassword()));
        }

        // Save the updated user
        User savedUser = userRepository.save(existingUser);
        return convertToDto(savedUser);
    }

    /**
     * Delete a user
     * @param id the user ID
     * @return true if the user was deleted, false if the user was not found
     */
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    /**
     * Convert a User entity to a UserDTO
     * @param user the user entity
     * @return the user DTO
     */
    private UserDTO convertToDto(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
