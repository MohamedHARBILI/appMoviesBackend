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
 * Service for watchlist-related operations
 * 
 * This service handles all business logic related to watchlists, including:
 * - Creating, reading, updating, and deleting watchlists
 * - Managing movies within watchlists (adding, updating, removing)
 * - Converting between entity and DTO objects
 * 
 * A service in Spring is a class that contains business logic and sits between
 * controllers (which handle HTTP requests) and repositories (which handle database access).
 */
@Service
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemService watchlistItemService;

    public WatchlistService(WatchlistRepository watchlistRepository, 
                           WatchlistItemService watchlistItemService) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemService = watchlistItemService;
    }

    /**
     * Get all watchlists for a user
     * 
     * @param userId the user ID
     * @return list of watchlist DTOs
     */
    public List<WatchlistDTO> getWatchlistsByUserId(Long userId) {
        // Step 1: Retrieve all watchlists for the user from the database
        List<Watchlist> watchlists = watchlistRepository.findByUserId(userId);

        // Step 2: Convert each Watchlist entity to a WatchlistDTO
        // The following code uses Java Streams to process the list:
        // 1. stream() - Converts the list to a stream for processing
        // 2. map() - Transforms each Watchlist to a WatchlistDTO using the convertToDto method
        // 3. collect() - Gathers the results back into a List
        return watchlists.stream()
                .map(this::convertToDto)  // Same as: watchlist -> convertToDto(watchlist)
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
     * 
     * This method creates a new watchlist for a user after performing validations:
     * 1. Checks that the watchlist and its name are not null
     * 2. Verifies that the user doesn't already have a watchlist with the same name
     * 
     * @param watchlist the watchlist to create
     * @param userId the user ID
     * @return the created watchlist as DTO, or null if validation fails
     */
    public WatchlistDTO createWatchlist(Watchlist watchlist, Long userId) {
        // Step 1: Basic validation - ensure required data is present
        if (watchlist == null || watchlist.getName() == null || userId == null) {
            // If any required data is missing, return null to indicate failure
            return null;
        }

        // Step 2: Check if a watchlist with the same name already exists for this user
        // First, get all watchlists for the user
        List<Watchlist> userWatchlists = watchlistRepository.findByUserId(userId);

        // Then, check if any of those watchlists have the same name as the new one
        // The anyMatch method returns true if any element matches the condition
        boolean nameExists = userWatchlists.stream()
                .anyMatch(w -> w.getName().equals(watchlist.getName()));

        // If a watchlist with the same name exists, return null to indicate failure
        if (nameExists) {
            return null;
        }

        // Step 3: If all validations pass, save the watchlist to the database
        Watchlist savedWatchlist = watchlistRepository.save(watchlist);

        // Step 4: Convert the saved entity to a DTO and return it
        return convertToDto(savedWatchlist);
    }

    /**
     * Update a watchlist
     * 
     * This method updates an existing watchlist after performing several validations:
     * 1. Checks that the watchlist exists
     * 2. Ensures the updated data is valid
     * 3. If the name is being changed, verifies it doesn't conflict with other watchlists
     * 
     * @param id the watchlist ID
     * @param updatedWatchlist the updated watchlist data
     * @return the updated watchlist as DTO, or null if validation fails
     */
    public WatchlistDTO updateWatchlist(Long id, Watchlist updatedWatchlist) {
        // Step 1: Find the existing watchlist in the database
        Watchlist existingWatchlist = watchlistRepository.findById(id).orElse(null);

        // If the watchlist doesn't exist, return null to indicate failure
        if (existingWatchlist == null) {
            return null;
        }

        // Step 2: Basic validation - ensure required data is present
        if (updatedWatchlist == null || updatedWatchlist.getName() == null) {
            // If any required data is missing, return null to indicate failure
            return null;
        }

        // Step 3: Check if the name is being changed
        if (!updatedWatchlist.getName().equals(existingWatchlist.getName())) {
            // If the name is changing, we need to make sure it doesn't conflict with other watchlists

            // Get all watchlists for the user
            List<Watchlist> userWatchlists = watchlistRepository.findByUserId(existingWatchlist.getUser().getId());

            // Check if any OTHER watchlist (excluding this one) has the same name
            // This uses two stream operations:
            // 1. filter() - Excludes the current watchlist from consideration
            // 2. anyMatch() - Checks if any remaining watchlist has the new name
            boolean nameExists = userWatchlists.stream()
                    .filter(w -> !w.getId().equals(id))  // Exclude the current watchlist
                    .anyMatch(w -> w.getName().equals(updatedWatchlist.getName()));  // Check for name match

            // If another watchlist already has this name, return null to indicate failure
            if (nameExists) {
                return null;
            }
        }

        // Step 4: Update the watchlist fields with the new values
        existingWatchlist.setName(updatedWatchlist.getName());
        existingWatchlist.setDescription(updatedWatchlist.getDescription());

        // Step 5: Save the updated watchlist to the database
        Watchlist savedWatchlist = watchlistRepository.save(existingWatchlist);

        // Step 6: Convert the saved entity to a DTO and return it
        return convertToDto(savedWatchlist);
    }

    /**
     * Delete a watchlist
     * 
     * This method removes a watchlist from the database. It first checks if the
     * watchlist exists before attempting to delete it.
     * 
     * Note: Due to the cascade settings in the Watchlist entity, deleting a watchlist
     * will also delete all its associated watchlist items automatically.
     * 
     * @param id the watchlist ID to delete
     * @return true if the watchlist was successfully deleted, false if it wasn't found
     */
    public boolean deleteWatchlist(Long id) {
        // Step 1: Check if the watchlist exists
        if (!watchlistRepository.existsById(id)) {
            // If it doesn't exist, return false to indicate nothing was deleted
            return false;
        }

        // Step 2: Delete the watchlist
        // This will also delete all associated watchlist items due to cascade settings
        watchlistRepository.deleteById(id);

        // Step 3: Return true to indicate successful deletion
        return true;
    }

    /**
     * Add a movie to a watchlist
     * 
     * This method adds a movie to a watchlist with a specified status (e.g., "À_VOIR", "VU", "EN_COURS").
     * It first verifies that the watchlist exists before delegating the actual addition to the WatchlistItemService.
     * 
     * This demonstrates the principle of separation of concerns:
     * - WatchlistService handles operations on watchlists
     * - WatchlistItemService handles operations on individual items within watchlists
     * 
     * @param watchlistId the watchlist ID
     * @param movieId the movie ID to add
     * @param status the initial status of the movie in the watchlist
     * @return the created watchlist item as DTO, or null if the watchlist doesn't exist
     */
    public WatchlistItemDTO addMovieToWatchlist(Long watchlistId, Long movieId, WatchlistStatus status) {
        // Step 1: Check if the watchlist exists
        if (!watchlistRepository.existsById(watchlistId)) {
            // If the watchlist doesn't exist, return null to indicate failure
            return null;
        }

        // Step 2: Delegate to the WatchlistItemService
        // This is an example of the delegation pattern - we're delegating the responsibility
        // to another service that specializes in handling watchlist items
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
     * 
     * This method demonstrates the Data Transfer Object (DTO) pattern:
     * - Entities (like Watchlist) represent database tables and contain JPA annotations
     * - DTOs (like WatchlistDTO) are simpler objects used to transfer data to the client
     * 
     * Benefits of using DTOs:
     * 1. Security - Hide sensitive or internal data from clients
     * 2. Performance - Transfer only the needed data
     * 3. Flexibility - Shape the data in a way that's convenient for clients
     * 
     * @param watchlist the watchlist entity from the database
     * @return a WatchlistDTO containing only the data needed by clients
     */
    private WatchlistDTO convertToDto(Watchlist watchlist) {
        // Get all items in this watchlist, already converted to DTOs
        List<WatchlistItemDTO> itemDTOs = getWatchlistItems(watchlist.getId());

        // Create a new WatchlistDTO with only the data we want to expose to clients
        return new WatchlistDTO(
                watchlist.getId(),          // Include the ID for reference
                watchlist.getName(),        // Include the name
                watchlist.getDescription(), // Include the description
                itemDTOs                    // Include the items (also as DTOs)
        );

        // Note: We don't include the User object to avoid exposing user details
    }
}
