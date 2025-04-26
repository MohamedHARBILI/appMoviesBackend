package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.WatchlistDTO;
import com.harbili.appmoviesbackend.dto.WatchlistItemDTO;
import com.harbili.appmoviesbackend.entities.Watchlist;
import com.harbili.appmoviesbackend.entities.WatchlistStatus;
import com.harbili.appmoviesbackend.repositories.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is deprecated and should not be used.
 * Please use WatchlistService.java instead.
 * This file will be removed in a future update.
 */
// @Service annotation removed to prevent conflicts with WatchlistService
public class WatchlistServiceUpdated {
    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemService watchlistItemService;

    public WatchlistServiceUpdated(WatchlistRepository watchlistRepository, 
                           WatchlistItemService watchlistItemService) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemService = watchlistItemService;
    }

    /**
     * Get all watchlists for a user
     * @param userId the user ID
     * @return list of watchlist DTOs
     */
    public List<WatchlistDTO> getWatchlistsByUserId(Long userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserId(userId);
        return watchlists.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a watchlist by ID
     * @param id the watchlist ID
     * @return the watchlist DTO if found, otherwise null
     */
    public WatchlistDTO getWatchlistById(Long id) {
        return watchlistRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * Create a new watchlist
     * @param watchlist the watchlist to create
     * @param userId the user ID
     * @return the created watchlist as DTO
     */
    public WatchlistDTO createWatchlist(Watchlist watchlist, Long userId) {
        // Basic validation
        if (watchlist == null || watchlist.getName() == null || userId == null) {
            return null;
        }

        // Check if a watchlist with the same name already exists for this user
        List<Watchlist> userWatchlists = watchlistRepository.findByUserId(userId);
        boolean nameExists = userWatchlists.stream()
                .anyMatch(w -> w.getName().equals(watchlist.getName()));

        if (nameExists) {
            return null;
        }

        // Save the watchlist
        Watchlist savedWatchlist = watchlistRepository.save(watchlist);
        return convertToDto(savedWatchlist);
    }

    /**
     * Update a watchlist
     * @param id the watchlist ID
     * @param updatedWatchlist the updated watchlist data
     * @return the updated watchlist as DTO
     */
    public WatchlistDTO updateWatchlist(Long id, Watchlist updatedWatchlist) {
        // Find the existing watchlist
        Watchlist existingWatchlist = watchlistRepository.findById(id).orElse(null);
        if (existingWatchlist == null) {
            return null;
        }

        // Basic validation
        if (updatedWatchlist == null || updatedWatchlist.getName() == null) {
            return null;
        }

        // Check if name is being changed and is already taken by another watchlist of the same user
        if (!updatedWatchlist.getName().equals(existingWatchlist.getName())) {
            List<Watchlist> userWatchlists = watchlistRepository.findByUserId(existingWatchlist.getUser().getId());
            boolean nameExists = userWatchlists.stream()
                    .filter(w -> !w.getId().equals(id))
                    .anyMatch(w -> w.getName().equals(updatedWatchlist.getName()));

            if (nameExists) {
                return null;
            }
        }

        // Update fields
        existingWatchlist.setName(updatedWatchlist.getName());
        existingWatchlist.setDescription(updatedWatchlist.getDescription());

        // Save the updated watchlist
        Watchlist savedWatchlist = watchlistRepository.save(existingWatchlist);
        return convertToDto(savedWatchlist);
    }

    /**
     * Delete a watchlist
     * @param id the watchlist ID
     * @return true if the watchlist was deleted, false if the watchlist was not found
     */
    public boolean deleteWatchlist(Long id) {
        if (!watchlistRepository.existsById(id)) {
            return false;
        }
        watchlistRepository.deleteById(id);
        return true;
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
        if (!watchlistRepository.existsById(watchlistId)) {
            return null;
        }

        // Delegate to the WatchlistItemService
        return watchlistItemService.addMovieToWatchlist(watchlistId, movieId, status);
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
        // Delegate to the WatchlistItemService
        return watchlistItemService.updateWatchlistItem(itemId, status, rating, notes);
    }

    /**
     * Remove a movie from a watchlist
     * @param itemId the watchlist item ID
     * @return true if the item was removed, false if the item was not found
     */
    public boolean removeMovieFromWatchlist(Long itemId) {
        // Delegate to the WatchlistItemService
        return watchlistItemService.removeMovieFromWatchlist(itemId);
    }

    /**
     * Get all items in a watchlist
     * @param watchlistId the watchlist ID
     * @return list of watchlist item DTOs
     */
    public List<WatchlistItemDTO> getWatchlistItems(Long watchlistId) {
        // Delegate to the WatchlistItemService
        return watchlistItemService.getWatchlistItems(watchlistId);
    }

    /**
     * Get all items with a specific status in a watchlist
     * @param watchlistId the watchlist ID
     * @param status the status to filter by
     * @return list of watchlist item DTOs
     */
    public List<WatchlistItemDTO> getWatchlistItemsByStatus(Long watchlistId, WatchlistStatus status) {
        // Delegate to the WatchlistItemService
        return watchlistItemService.getWatchlistItemsByStatus(watchlistId, status);
    }

    /**
     * Convert a Watchlist entity to a WatchlistDTO
     * @param watchlist the watchlist entity
     * @return the watchlist DTO
     */
    private WatchlistDTO convertToDto(Watchlist watchlist) {
        List<WatchlistItemDTO> itemDTOs = watchlistItemService.getWatchlistItems(watchlist.getId());
        return new WatchlistDTO(
                watchlist.getId(),
                watchlist.getName(),
                watchlist.getDescription(),
                itemDTOs
        );
    }
}
