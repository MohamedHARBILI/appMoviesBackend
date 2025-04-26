package com.harbili.appmoviesbackend.controllers;

import com.harbili.appmoviesbackend.dto.WatchlistItemDTO;
import com.harbili.appmoviesbackend.entities.WatchlistStatus;
import com.harbili.appmoviesbackend.services.WatchlistItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for watchlist item-related operations
 */
@RestController
@RequestMapping("/api/watchlist-items")
@CrossOrigin(origins = "*")
public class WatchlistItemController {
    private final WatchlistItemService watchlistItemService;

    public WatchlistItemController(WatchlistItemService watchlistItemService) {
        this.watchlistItemService = watchlistItemService;
    }

    /**
     * Get a watchlist item by ID
     * @param id the watchlist item ID
     * @return the watchlist item if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getWatchlistItemById(@PathVariable Long id) {
        try {
            WatchlistItemDTO item = watchlistItemService.getWatchlistItemById(id);
            if (item == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Watchlist item not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error retrieving watchlist item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all items in a watchlist
     * @param watchlistId the watchlist ID
     * @return list of watchlist items
     */
    @GetMapping("/watchlist/{watchlistId}")
    public ResponseEntity<Object> getWatchlistItems(@PathVariable Long watchlistId) {
        try {
            List<WatchlistItemDTO> items = watchlistItemService.getWatchlistItems(watchlistId);
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
    @GetMapping("/watchlist/{watchlistId}/status/{status}")
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
            
            List<WatchlistItemDTO> items = watchlistItemService.getWatchlistItemsByStatus(watchlistId, watchlistStatus);
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
    @PostMapping("/watchlist/{watchlistId}")
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
            
            WatchlistItemDTO item = watchlistItemService.addMovieToWatchlist(watchlistId, movieId, watchlistStatus);
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
    @PutMapping("/{itemId}")
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
            
            WatchlistItemDTO item = watchlistItemService.updateWatchlistItem(itemId, watchlistStatus, rating, notes);
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
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> removeMovieFromWatchlist(@PathVariable Long itemId) {
        try {
            boolean removed = watchlistItemService.removeMovieFromWatchlist(itemId);
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