package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.MovieDTO;
import com.harbili.appmoviesbackend.dto.WatchlistItemDTO;
import com.harbili.appmoviesbackend.entities.Watchlist;
import com.harbili.appmoviesbackend.entities.WatchlistItem;
import com.harbili.appmoviesbackend.entities.WatchlistStatus;
import com.harbili.appmoviesbackend.repositories.WatchlistItemRepository;
import com.harbili.appmoviesbackend.repositories.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for watchlist item-related operations
 */
@Service
public class WatchlistItemService {
    private final WatchlistItemRepository watchlistItemRepository;
    private final WatchlistRepository watchlistRepository;
    private final MovieService movieService;

    public WatchlistItemService(WatchlistItemRepository watchlistItemRepository,
                               WatchlistRepository watchlistRepository,
                               MovieService movieService) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.watchlistRepository = watchlistRepository;
        this.movieService = movieService;
    }

    /**
     * Add a movie to a watchlist
     * @param watchlistId the watchlist ID
     * @param movieId the movie ID
     * @param status the status of the movie in the watchlist
     * @return the created watchlist item as DTO
     */
    public WatchlistItemDTO addMovieToWatchlist(Long watchlistId, Long movieId, WatchlistStatus status) {
        // Check if the watchlist exists
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElse(null);
        if (watchlist == null) {
            return null;
        }
        
        // Check if the movie exists
        MovieDTO movie = movieService.getMovieById(movieId);
        if (movie == null) {
            return null;
        }
        
        // Check if the movie is already in the watchlist
        if (watchlistItemRepository.existsByWatchlistIdAndMovieId(watchlistId, movieId)) {
            return null;
        }
        
        // Create and save the watchlist item
        WatchlistItem watchlistItem = new WatchlistItem();
        watchlistItem.setWatchlist(watchlist);
        watchlistItem.setMovieId(movieId);
        watchlistItem.setStatus(status);
        
        WatchlistItem savedItem = watchlistItemRepository.save(watchlistItem);
        
        // Convert to DTO
        return new WatchlistItemDTO(
                savedItem.getId(),
                savedItem.getMovieId(),
                movie.getTitle(),
                savedItem.getStatus().toString(),
                savedItem.getRating(),
                savedItem.getNotes()
        );
    }

    /**
     * Update a watchlist item
     * @param itemId the watchlist item ID
     * @param status the new status
     * @param rating the new rating
     * @param notes the new notes
     * @return the updated watchlist item as DTO
     */
    public WatchlistItemDTO updateWatchlistItem(Long itemId, WatchlistStatus status, Integer rating, String notes) {
        // Find the existing item
        WatchlistItem item = watchlistItemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return null;
        }
        
        // Update fields
        if (status != null) {
            item.setStatus(status);
        }
        
        if (rating != null) {
            item.setRating(rating);
        }
        
        if (notes != null) {
            item.setNotes(notes);
        }
        
        // Save the updated item
        WatchlistItem savedItem = watchlistItemRepository.save(item);
        
        // Get movie details
        MovieDTO movie = movieService.getMovieById(savedItem.getMovieId());
        String movieTitle = movie != null ? movie.getTitle() : "Unknown";
        
        // Convert to DTO
        return new WatchlistItemDTO(
                savedItem.getId(),
                savedItem.getMovieId(),
                movieTitle,
                savedItem.getStatus().toString(),
                savedItem.getRating(),
                savedItem.getNotes()
        );
    }

    /**
     * Remove a movie from a watchlist
     * @param itemId the watchlist item ID
     * @return true if the item was removed, false if the item was not found
     */
    public boolean removeMovieFromWatchlist(Long itemId) {
        if (!watchlistItemRepository.existsById(itemId)) {
            return false;
        }
        watchlistItemRepository.deleteById(itemId);
        return true;
    }

    /**
     * Get all items in a watchlist
     * @param watchlistId the watchlist ID
     * @return list of watchlist item DTOs
     */
    public List<WatchlistItemDTO> getWatchlistItems(Long watchlistId) {
        List<WatchlistItem> items = watchlistItemRepository.findByWatchlistId(watchlistId);
        List<WatchlistItemDTO> itemDTOs = new ArrayList<>();
        
        for (WatchlistItem item : items) {
            MovieDTO movie = movieService.getMovieById(item.getMovieId());
            String movieTitle = movie != null ? movie.getTitle() : "Unknown";
            
            WatchlistItemDTO itemDTO = new WatchlistItemDTO(
                    item.getId(),
                    item.getMovieId(),
                    movieTitle,
                    item.getStatus().toString(),
                    item.getRating(),
                    item.getNotes()
            );
            
            itemDTOs.add(itemDTO);
        }
        
        return itemDTOs;
    }

    /**
     * Get all items with a specific status in a watchlist
     * @param watchlistId the watchlist ID
     * @param status the status to filter by
     * @return list of watchlist item DTOs
     */
    public List<WatchlistItemDTO> getWatchlistItemsByStatus(Long watchlistId, WatchlistStatus status) {
        List<WatchlistItem> items = watchlistItemRepository.findByWatchlistIdAndStatus(watchlistId, status);
        List<WatchlistItemDTO> itemDTOs = new ArrayList<>();
        
        for (WatchlistItem item : items) {
            MovieDTO movie = movieService.getMovieById(item.getMovieId());
            String movieTitle = movie != null ? movie.getTitle() : "Unknown";
            
            WatchlistItemDTO itemDTO = new WatchlistItemDTO(
                    item.getId(),
                    item.getMovieId(),
                    movieTitle,
                    item.getStatus().toString(),
                    item.getRating(),
                    item.getNotes()
            );
            
            itemDTOs.add(itemDTO);
        }
        
        return itemDTOs;
    }

    /**
     * Get a watchlist item by ID
     * @param itemId the watchlist item ID
     * @return the watchlist item DTO if found, otherwise null
     */
    public WatchlistItemDTO getWatchlistItemById(Long itemId) {
        WatchlistItem item = watchlistItemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return null;
        }
        
        MovieDTO movie = movieService.getMovieById(item.getMovieId());
        String movieTitle = movie != null ? movie.getTitle() : "Unknown";
        
        return new WatchlistItemDTO(
                item.getId(),
                item.getMovieId(),
                movieTitle,
                item.getStatus().toString(),
                item.getRating(),
                item.getNotes()
        );
    }
}