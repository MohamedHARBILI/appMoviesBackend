package com.harbili.appmoviesbackend.config;

import com.harbili.appmoviesbackend.entities.User;
import com.harbili.appmoviesbackend.entities.Watchlist;
import com.harbili.appmoviesbackend.entities.WatchlistItem;
import com.harbili.appmoviesbackend.repositories.MovieRepository;
import com.harbili.appmoviesbackend.repositories.UserRepository;
import com.harbili.appmoviesbackend.repositories.WatchlistItemRepository;
import com.harbili.appmoviesbackend.repositories.WatchlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchlistItemRepository watchlistItemRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void testDataInitialization() {
        // Test that users were created
        List<User> users = userRepository.findAll();
        System.out.println("[DEBUG_LOG] Found " + users.size() + " users");
        assertTrue(users.size() >= 3, "At least 3 users should be created");
        
        // Verify specific user exists
        User user1 = userRepository.findByUsername("user1");
        assertNotNull(user1, "User 'user1' should exist");
        assertEquals("user1@example.com", user1.getEmail(), "User email should match");
        
        // Test that movies were created
        long movieCount = movieRepository.count();
        System.out.println("[DEBUG_LOG] Found " + movieCount + " movies");
        assertTrue(movieCount >= 3, "At least 3 movies should be created");
        
        // Test that watchlists were created
        List<Watchlist> watchlists = watchlistRepository.findAll();
        System.out.println("[DEBUG_LOG] Found " + watchlists.size() + " watchlists");
        assertTrue(watchlists.size() >= 3, "At least 3 watchlists should be created");
        
        // Verify specific watchlist exists
        List<Watchlist> userWatchlists = watchlistRepository.findByUserId(user1.getId());
        assertFalse(userWatchlists.isEmpty(), "User1 should have watchlists");
        
        // Test that watchlist items were created
        List<WatchlistItem> watchlistItems = watchlistItemRepository.findAll();
        System.out.println("[DEBUG_LOG] Found " + watchlistItems.size() + " watchlist items");
        assertTrue(watchlistItems.size() >= 4, "At least 4 watchlist items should be created");
        
        // Verify items in a specific watchlist
        if (!userWatchlists.isEmpty()) {
            Watchlist firstWatchlist = userWatchlists.get(0);
            List<WatchlistItem> items = watchlistItemRepository.findByWatchlistId(firstWatchlist.getId());
            assertFalse(items.isEmpty(), "Watchlist should have items");
            System.out.println("[DEBUG_LOG] Watchlist '" + firstWatchlist.getName() + "' has " + items.size() + " items");
        }
    }
}