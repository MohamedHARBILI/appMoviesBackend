package com.harbili.appmoviesbackend.controllers;

import com.harbili.appmoviesbackend.dto.WatchlistDTO;
import com.harbili.appmoviesbackend.dto.WatchlistItemDTO;
import com.harbili.appmoviesbackend.entities.Watchlist;
import com.harbili.appmoviesbackend.entities.WatchlistStatus;
import com.harbili.appmoviesbackend.services.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for watchlist-related operations
 */
@RestController
@RequestMapping("/api/watchlists")
@CrossOrigin(origins = "*")
public class WatchlistController {
    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    /**
     * Get all watchlists for a user
     * @param userId the user ID
     * @return list of watchlists
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WatchlistDTO>> getWatchlistsByUserId(@PathVariable Long userId) {
        try {
            List<WatchlistDTO> watchlists = watchlistService.getWatchlistsByUserId(userId);
            return ResponseEntity.ok(watchlists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a watchlist by ID
     * @param id the watchlist ID
     * @return the watchlist if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getWatchlistById(@PathVariable Long id) {
        try {
            WatchlistDTO watchlist = watchlistService.getWatchlistById(id);
            if (watchlist == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Watchlist not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(watchlist);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error retrieving watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new watchlist
     * @param watchlist the watchlist to create
     * @param userId the user ID
     * @return the created watchlist
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<Object> createWatchlist(@RequestBody Watchlist watchlist, @PathVariable Long userId) {
        try {
            WatchlistDTO createdWatchlist = watchlistService.createWatchlist(watchlist, userId);
            if (createdWatchlist == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Failed to create watchlist. Name may already be taken.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWatchlist);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error creating watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update a watchlist
     * @param id the watchlist ID
     * @param watchlist the updated watchlist data
     * @return the updated watchlist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateWatchlist(@PathVariable Long id, @RequestBody Watchlist watchlist) {
        try {
            WatchlistDTO updatedWatchlist = watchlistService.updateWatchlist(id, watchlist);
            if (updatedWatchlist == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Failed to update watchlist. Watchlist not found or name already taken.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(updatedWatchlist);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a watchlist
     * @param id the watchlist ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWatchlist(@PathVariable Long id) {
        try {
            boolean deleted = watchlistService.deleteWatchlist(id);
            if (!deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Watchlist not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error deleting watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all items in a watchlist
     * @param watchlistId the watchlist ID
     * @return list of watchlist items
     */
    @GetMapping("/{watchlistId}/items")
    public ResponseEntity<Object> getWatchlistItems(@PathVariable Long watchlistId) {
        try {
            List<WatchlistItemDTO> items = watchlistService.getWatchlistItems(watchlistId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error retrieving watchlist items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all items with a specific status in a watchlist
     * @param watchlistId the watchlist ID
     * @param status the status to filter by
     * @return list of watchlist items
     */
    @GetMapping("/{watchlistId}/items/status/{status}")
    public ResponseEntity<Object> getWatchlistItemsByStatus(
            @PathVariable Long watchlistId,
            @PathVariable String status) {
        try {
            WatchlistStatus watchlistStatus;
            try {
                watchlistStatus = WatchlistStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid status: " + status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            List<WatchlistItemDTO> items = watchlistService.getWatchlistItemsByStatus(watchlistId, watchlistStatus);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error retrieving watchlist items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add a movie to a watchlist
     * @param watchlistId the watchlist ID
     * @param movieId the movie ID
     * @param status the status of the movie in the watchlist
     * @return the created watchlist item
     */
    @PostMapping("/{watchlistId}/items")
    public ResponseEntity<Object> addMovieToWatchlist(
            @PathVariable Long watchlistId,
            @RequestParam Long movieId,
            @RequestParam(defaultValue = "À_VOIR") String status) {
        try {
            WatchlistStatus watchlistStatus;
            try {
                watchlistStatus = WatchlistStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid status: " + status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            WatchlistItemDTO item = watchlistService.addMovieToWatchlist(watchlistId, movieId, watchlistStatus);
            if (item == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Failed to add movie to watchlist. Watchlist or movie not found, or movie already in watchlist.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error adding movie to watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update a watchlist item
     * @param itemId the watchlist item ID
     * @param status the new status
     * @param rating the new rating
     * @param notes the new notes
     * @return the updated watchlist item
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Object> updateWatchlistItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String notes) {
        try {
            WatchlistStatus watchlistStatus = null;
            if (status != null) {
                try {
                    watchlistStatus = WatchlistStatus.valueOf(status);
                } catch (IllegalArgumentException e) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Invalid status: " + status);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
            
            WatchlistItemDTO item = watchlistService.updateWatchlistItem(itemId, watchlistStatus, rating, notes);
            if (item == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Watchlist item not found with id: " + itemId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating watchlist item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove a movie from a watchlist
     * @param itemId the watchlist item ID
     * @return no content if successful
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Object> removeMovieFromWatchlist(@PathVariable Long itemId) {
        try {
            boolean removed = watchlistService.removeMovieFromWatchlist(itemId);
            if (!removed) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Watchlist item not found with id: " + itemId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error removing movie from watchlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}