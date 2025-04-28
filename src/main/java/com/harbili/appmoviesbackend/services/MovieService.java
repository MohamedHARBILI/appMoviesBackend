package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.MovieDTO;
import com.harbili.appmoviesbackend.entities.Movie;
import com.harbili.appmoviesbackend.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.restTemplate = new RestTemplate();
    }

    // Opérations CRUD de base

    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        return movie != null ? convertToDto(movie) : null;
    }

    public MovieDTO saveMovie(Movie movie) {
        if (movie == null) return null;
        return convertToDto(movieRepository.save(movie));
    }

    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            movies = fetchMoviesFromTMDB();
            movieRepository.saveAll(movies);
        }
        return convertToDtoList(movies);
    }

    public List<MovieDTO> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return convertToDtoList(movieRepository.findByTitleContainingIgnoreCase(query));
    }

    public MovieDTO updateMovie(Long id, Movie updatedMovie) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) return null;

        // Mise à jour simple des propriétés
        movie.setTitle(updatedMovie.getTitle());
        movie.setOverview(updatedMovie.getOverview());
        movie.setPosterPath(updatedMovie.getPosterPath());
        movie.setReleaseDate(updatedMovie.getReleaseDate());

        return convertToDto(movieRepository.save(movie));
    }

    public boolean deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) return false;
        movieRepository.deleteById(id);
        return true;
    }

    // Récupération des films depuis TMDB (version simplifiée)
    private List<Movie> fetchMoviesFromTMDB() {
        List<Movie> movies = new ArrayList<>();
        int page = 1;
        boolean hasMorePages = true;

        try {
            while (hasMorePages) {
                String url = String.format("%s/discover/movie?api_key=%s&page=%d", baseUrl, apiKey, page);
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response == null || !(response.get("results") instanceof List)) {
                    break;
                }

                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                for (Map<String, Object> movieData : results) {
                    Movie movie = new Movie();
                    movie.setId(((Number) movieData.get("id")).longValue());
                    movie.setTitle((String) movieData.get("title"));
                    movie.setOverview((String) movieData.get("overview"));
                    movie.setPosterPath((String) movieData.get("poster_path"));

                    String releaseDate = (String) movieData.get("release_date");
                    if (releaseDate != null) {
                        movie.setReleaseDate(LocalDate.parse(releaseDate));
                    }

                    movies.add(movie);
                }

                // Vérifie s'il y a encore des pages
                int totalPages = (int) response.getOrDefault("total_pages", 1);
                hasMorePages = page < totalPages;
                page++;
            }
        } catch (Exception e) {
            System.err.println("Error fetching movies from TMDB: " + e.getMessage());
        }

        return movies;
    }

    // Conversion en DTO
    private MovieDTO convertToDto(Movie movie) {
        if (movie == null) return null;

        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getPosterPath() != null ?
                        "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null,
                movie.getReleaseDate(),
                new ArrayList<>()
        );
    }

    // Conversion d'une liste de films
    private List<MovieDTO> convertToDtoList(List<Movie> movies) {
        List<MovieDTO> dtos = new ArrayList<>();
        for (Movie movie : movies) {
            dtos.add(convertToDto(movie));
        }
        return dtos;
    }
}