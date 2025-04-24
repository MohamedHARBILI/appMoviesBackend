package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndEmail(String username, String email);

}
