package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Integer> {
    List<WatchlistItem> findByWatchlistId(Long watchlistId);
    boolean existsByWatchlistIdAndMovieId(Long watchlistId, Long movieId);
}
