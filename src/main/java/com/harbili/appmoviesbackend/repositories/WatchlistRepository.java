package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Watchlist entity
 */
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    /**
     * Find a watchlist by its name
     * @param name the name to search for
     * @return the watchlist if found
     */
    Optional<Watchlist> findByName(String name);

    /**
     * Find all watchlists for a specific user
     * @param userId the user ID
     * @return list of watchlists
     */
    List<Watchlist> findByUserId(Long userId);
}
