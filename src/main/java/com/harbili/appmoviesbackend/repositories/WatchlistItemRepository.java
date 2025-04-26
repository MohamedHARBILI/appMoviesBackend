package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.WatchlistItem;
import com.harbili.appmoviesbackend.entities.WatchlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for WatchlistItem entity
 */
public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    /**
     * Find all items in a specific watchlist
     * @param watchlistId the watchlist ID
     * @return list of watchlist items
     */
    List<WatchlistItem> findByWatchlistId(Long watchlistId);

    /**
     * Check if a movie is already in a specific watchlist
     * @param watchlistId the watchlist ID
     * @param movieId the movie ID
     * @return true if the movie is in the watchlist
     */
    boolean existsByWatchlistIdAndMovieId(Long watchlistId, Long movieId);

    /**
     * Find all items with a specific status in a watchlist
     * @param watchlistId the watchlist ID
     * @param status the status to filter by
     * @return list of watchlist items
     */
    List<WatchlistItem> findByWatchlistIdAndStatus(Long watchlistId, WatchlistStatus status);
}
