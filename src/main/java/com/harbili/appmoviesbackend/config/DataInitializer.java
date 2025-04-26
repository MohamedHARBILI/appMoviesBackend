package com.harbili.appmoviesbackend.config;

import com.harbili.appmoviesbackend.entities.*;
import com.harbili.appmoviesbackend.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Component to initialize sample data at application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final MovieRepository movieRepository;

    public DataInitializer(
            UserRepository userRepository,
            WatchlistRepository watchlistRepository,
            WatchlistItemRepository watchlistItemRepository,
            MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Check if data already exists
        if (userRepository.count() > 0) {
            System.out.println("Data already initialized, skipping...");
            return;
        }

        System.out.println("Initializing sample data...");
        
        // Create and save sample movies
        createSampleMovies();
        
        // Create and save sample users
        List<User> users = createSampleUsers();
        
        // Create and save sample watchlists with items
        createSampleWatchlists(users);
        
        System.out.println("Sample data initialization completed!");
    }

    private List<User> createSampleUsers() {
        System.out.println("Creating sample users...");
        
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .role("USER")
                .build();
        
        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password2")
                .role("USER")
                .build();
        
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("adminpass")
                .role("ADMIN")
                .build();
        
        return userRepository.saveAll(Arrays.asList(user1, user2, admin));
    }

    private void createSampleMovies() {
        System.out.println("Creating sample movies...");
        
        Movie movie1 = Movie.builder()
                .id(550L) // Fight Club (using TMDb ID)
                .title("Fight Club")
                .overview("A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy.")
                .posterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg")
                .releaseDate(LocalDate.of(1999, 10, 15))
                .genres(Arrays.asList("Drama", "Thriller"))
                .build();
        
        Movie movie2 = Movie.builder()
                .id(680L) // Pulp Fiction (using TMDb ID)
                .title("Pulp Fiction")
                .overview("A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper.")
                .posterPath("/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg")
                .releaseDate(LocalDate.of(1994, 10, 14))
                .genres(Arrays.asList("Thriller", "Crime"))
                .build();
        
        Movie movie3 = Movie.builder()
                .id(155L) // The Dark Knight (using TMDb ID)
                .title("The Dark Knight")
                .overview("Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets.")
                .posterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg")
                .releaseDate(LocalDate.of(2008, 7, 18))
                .genres(Arrays.asList("Action", "Crime", "Drama", "Thriller"))
                .build();
        
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3));
    }

    private void createSampleWatchlists(List<User> users) {
        System.out.println("Creating sample watchlists and watchlist items...");
        
        User user1 = users.get(0);
        User user2 = users.get(1);
        
        // Create watchlists for user1
        Watchlist favorites = Watchlist.builder()
                .name("Favorites")
                .description("My favorite movies of all time")
                .user(user1)
                .build();
        
        Watchlist toWatch = Watchlist.builder()
                .name("To Watch")
                .description("Movies I want to watch")
                .user(user1)
                .build();
        
        // Create watchlist for user2
        Watchlist classics = Watchlist.builder()
                .name("Classics")
                .description("Classic movies everyone should watch")
                .user(user2)
                .build();
        
        // Save watchlists
        watchlistRepository.saveAll(Arrays.asList(favorites, toWatch, classics));
        
        // Create watchlist items
        List<Movie> movies = movieRepository.findAll();
        
        // Items for "Favorites" watchlist
        WatchlistItem item1 = WatchlistItem.builder()
                .watchlist(favorites)
                .movieId(movies.get(0).getId()) // Fight Club
                .status(WatchlistStatus.VU)
                .rating(5)
                .notes("Absolutely brilliant!")
                .build();
        
        // Items for "To Watch" watchlist
        WatchlistItem item2 = WatchlistItem.builder()
                .watchlist(toWatch)
                .movieId(movies.get(1).getId()) // Pulp Fiction
                .status(WatchlistStatus.À_VOIR)
                .build();
        
        // Items for "Classics" watchlist
        WatchlistItem item3 = WatchlistItem.builder()
                .watchlist(classics)
                .movieId(movies.get(0).getId()) // Fight Club
                .status(WatchlistStatus.VU)
                .rating(4)
                .notes("A modern classic")
                .build();
        
        WatchlistItem item4 = WatchlistItem.builder()
                .watchlist(classics)
                .movieId(movies.get(2).getId()) // The Dark Knight
                .status(WatchlistStatus.EN_COURS)
                .build();
        
        // Save watchlist items
        watchlistItemRepository.saveAll(Arrays.asList(item1, item2, item3, item4));
    }
}