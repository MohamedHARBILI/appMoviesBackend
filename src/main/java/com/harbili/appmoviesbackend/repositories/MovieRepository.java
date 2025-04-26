package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Movie entity
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {
    /**
     * Find a movie by its exact title
     * @param title the title to search for
     * @return the movie if found
     */
    Optional<Movie> findByTitle(String title);

    /**
     * Find a movie by its poster path
     * @param posterPath the poster path to search for
     * @return the movie if found
     */
    Optional<Movie> findByPosterPath(String posterPath);

    /**
     * Find a movie by its overview
     * @param overview the overview to search for
     * @return the movie if found
     */
    Optional<Movie> findByOverview(String overview);

    /**
     * Find movies with titles containing the given query (case insensitive)
     * @param query the query to search for
     * @return list of matching movies
     */
    List<Movie> findByTitleContainingIgnoreCase(String query);

    /**
     * Find recently added movies
     * @return list of movies ordered by ID (descending)
     */
    @Query("SELECT m FROM Movie m ORDER BY m.id DESC")
    List<Movie> findRecentMovies();
}
