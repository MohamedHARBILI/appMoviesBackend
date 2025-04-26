package com.harbili.appmoviesbackend.controllers;

import com.harbili.appmoviesbackend.dto.MovieDTO;
import com.harbili.appmoviesbackend.entities.Movie;
import com.harbili.appmoviesbackend.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for movie-related operations
 */
@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Get all movies
     * @return list of all movies
     */
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        try {
            List<MovieDTO> movies = movieService.findAllMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all movies with pagination
     * @param page the page number
     * @param size the page size
     * @param sort the sort field
     * @param direction the sort direction
     * @return page of movies
     */
    @GetMapping("/page")
    public ResponseEntity<Page<MovieDTO>> getMoviesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<MovieDTO> movies = movieService.findAllMovies(pageable);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a movie by ID
     * @param id the movie ID
     * @return the movie if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMovieById(@PathVariable Long id) {
        try {
            MovieDTO movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Movie not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Search for movies by title
     * @param query the search query
     * @return list of matching movies
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam String query) {
        try {
            List<MovieDTO> movies = movieService.searchMovies(query);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recent movies
     * @return list of recent movies
     */
    @GetMapping("/recent")
    public ResponseEntity<List<MovieDTO>> getRecentMovies() {
        try {
            List<MovieDTO> movies = movieService.getRecentMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Fetch a movie from TMDb
     * @param tmdbId the TMDb ID
     * @return the fetched movie
     */
    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<Object> fetchFromTmdb(@PathVariable Long tmdbId) {
        try {
            MovieDTO movie = movieService.fetchFromTmdb(tmdbId);
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to fetch movie from TMDb: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Create a new movie
     * @param movie the movie to create
     * @return the created movie
     */
    @PostMapping
    public ResponseEntity<Object> createMovie(@RequestBody Movie movie) {
        try {
            MovieDTO createdMovie = movieService.saveMovie(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create movie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Update a movie
     * @param id the movie ID
     * @param movie the updated movie data
     * @return the updated movie
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            MovieDTO updatedMovie = movieService.updateMovie(id, movie);
            return ResponseEntity.ok(updatedMovie);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update movie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete a movie
     * @param id the movie ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable Long id) {
        boolean deleted = movieService.deleteMovie(id);
        if (!deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete movie. Movie not found or error occurred.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.noContent().build();
    }
}
