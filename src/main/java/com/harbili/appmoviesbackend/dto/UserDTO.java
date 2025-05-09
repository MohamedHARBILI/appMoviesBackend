package com.harbili.appmoviesbackend.dto;

public class UserDTO {

        private Long id;
        private String username;
        private String email;

        // Constructeur
        public UserDTO(Long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }
    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

}
