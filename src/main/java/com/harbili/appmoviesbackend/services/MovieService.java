    package com.harbili.appmoviesbackend.services;

    import com.harbili.appmoviesbackend.dto.MovieDTO;
    import com.harbili.appmoviesbackend.entities.Movie;

    import com.harbili.appmoviesbackend.repositories.MovieRepository;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageImpl;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpMethod;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    /**
     * Service for movie-related operations
     */
    @Service
    public class MovieService {
        private final MovieRepository movieRepository;
        private final RestTemplate restTemplate;

        @Value("${tmdb.api.key}")
        private String apiKey;

        @Value("${tmdb.api.access-token}")
        private String accessToken;

        @Value("${tmdb.api.base-url}")
        private String baseUrl;

        public MovieService(MovieRepository movieRepository) {
            this.movieRepository = movieRepository;
            this.restTemplate = new RestTemplate();
        }

        /**
         * Get a movie by TMDb ID from the local database
         * @param tmdbId the TMDb ID
         * @return the movie DTO, or null if not found
         */
        public MovieDTO fetchFromTmdb(Long tmdbId) {
            // In this simplified version, we just look for the movie in our local database
            return movieRepository.findById(tmdbId)
                    .map(this::convertToDto)
                    .orElse(null);
        }
        /**
         * Get a movie by ID from the local database
         * @param id the movie ID
         * @return the movie DTO, or null if not found
         */
        public MovieDTO getMovieById(Long id) {
            return movieRepository.findById(id)
                    .map(this::convertToDto)
                    .orElse(null);
        }

        /**
         * Save a movie to the database
         * @param movie the movie to save
         * @return the saved movie as DTO, or null if movie is null
         */
        public MovieDTO saveMovie(Movie movie) {
            if (movie == null) {
                return null;
            }
            try {
                return convertToDto(movieRepository.save(movie));
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Find all movies in the database
         * If the database is empty, fetch popular movies from TMDb
         * @return list of all movies as DTOs
         */
        public List<MovieDTO> findAllMovies() {
            List<Movie> movies = movieRepository.findAll();

            // If no movies in database, fetch popular movies from TMDb
            if (movies.isEmpty()) {
                System.out.println("No movies found in database. Fetching popular movies from TMDb...");
                List<Movie> popularMovies = fetchPopularMoviesFromTmdb();
                if (!popularMovies.isEmpty()) {
                    movies = movieRepository.saveAll(popularMovies);
                    System.out.println("Saved " + movies.size() + " popular movies to database");
                }
            }

            return movies.stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /**
         * Find all movies with pagination
         * @param pageable pagination information
         * @return page of movies as DTOs
         */
        public Page<MovieDTO> findAllMovies(Pageable pageable) {
            return movieRepository.findAll(pageable).map(this::convertToDto);
        }

        /**
         * Search for movies by title
         * @param query the search query
         * @return list of matching movies as DTOs
         */
        public List<MovieDTO> searchMovies(String query) {
            if (query == null || query.isBlank()) {
                return List.of();
            }
            return movieRepository.findByTitleContainingIgnoreCase(query).stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /**
         * Update a movie
         * @param id the movie ID
         * @param updatedMovie the updated movie data
         * @return the updated movie as DTO, or null if movie is not found or error occurs
         */
        public MovieDTO updateMovie(Long id, Movie updatedMovie) {
            try {
                return movieRepository.findById(id)
                        .map(movie -> {
                            movie.setTitle(updatedMovie.getTitle());
                            movie.setOverview(updatedMovie.getOverview());
                            movie.setPosterPath(updatedMovie.getPosterPath());
                            movie.setReleaseDate(updatedMovie.getReleaseDate());
                            movie.setGenres(updatedMovie.getGenres());
                            return convertToDto(movieRepository.save(movie));
                        })
                        .orElse(null);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Delete a movie
         * @param id the movie ID
         * @return true if the movie was deleted, false if the movie was not found or error occurs
         */
        public boolean deleteMovie(Long id) {
            try {
                if (!movieRepository.existsById(id)) {
                    return false;
                }
                movieRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Get recent movies
         * @return list of recent movies as DTOs
         */
        public List<MovieDTO> getRecentMovies() {
            return movieRepository.findRecentMovies().stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /**
         * Fetch popular movies from TMDb API
         * @return list of popular movies
         */
        private List<Movie> fetchPopularMoviesFromTmdb() {
            List<Movie> movies = new ArrayList<>();

            try {
                // Set up HTTP headers with authorization
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("accept", "application/json");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Fetch popular movies from TMDb API (first page)
                String url = baseUrl + "/movie/popular?language=en-US&page=1";
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

                    if (results != null) {
                        System.out.println("Fetched " + results.size() + " movies from TMDb API");

                        for (Map<String, Object> result : results) {
                            Movie movie = new Movie();

                            // Extract movie data from API response
                            Long id = ((Number) result.get("id")).longValue();
                            String title = (String) result.get("title");
                            String overview = (String) result.get("overview");
                            String posterPath = (String) result.get("poster_path");
                            String releaseDateStr = (String) result.get("release_date");

                            // Set movie properties
                            movie.setId(id);
                            movie.setTitle(title);
                            movie.setOverview(overview);
                            movie.setPosterPath(posterPath);

                            // Parse release date if available
                            if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                                try {
                                    LocalDate releaseDate = LocalDate.parse(releaseDateStr);
                                    movie.setReleaseDate(releaseDate);
                                } catch (Exception e) {
                                    System.out.println("Error parsing release date: " + releaseDateStr);
                                }
                            }

                            // Get genres for the movie
                            List<String> genres = fetchGenresForMovie(id);
                            movie.setGenres(genres);

                            movies.add(movie);
                        }
                    }
                }

                // Fetch more pages if needed
                int totalPages = 5; // Limit to 5 pages (100 movies) for performance
                for (int page = 2; page <= totalPages; page++) {
                    url = baseUrl + "/movie/popular?language=en-US&page=" + page;
                    response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

                        if (results != null) {
                            System.out.println("Fetched " + results.size() + " more movies from TMDb API (page " + page + ")");

                            for (Map<String, Object> result : results) {
                                Movie movie = new Movie();

                                // Extract movie data from API response
                                Long id = ((Number) result.get("id")).longValue();
                                String title = (String) result.get("title");
                                String overview = (String) result.get("overview");
                                String posterPath = (String) result.get("poster_path");
                                String releaseDateStr = (String) result.get("release_date");

                                // Set movie properties
                                movie.setId(id);
                                movie.setTitle(title);
                                movie.setOverview(overview);
                                movie.setPosterPath(posterPath);

                                // Parse release date if available
                                if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                                    try {
                                        LocalDate releaseDate = LocalDate.parse(releaseDateStr);
                                        movie.setReleaseDate(releaseDate);
                                    } catch (Exception e) {
                                        System.out.println("Error parsing release date: " + releaseDateStr);
                                    }
                                }

                                // Get genres for the movie
                                List<String> genres = fetchGenresForMovie(id);
                                movie.setGenres(genres);

                                movies.add(movie);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error fetching movies from TMDb: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("Fetched a total of " + movies.size() + " movies from TMDb API");
            return movies;
        }

        /**
         * Fetch genres for a specific movie from TMDb API
         * @param movieId the TMDb movie ID
         * @return list of genre names
         */
        private List<String> fetchGenresForMovie(Long movieId) {
            List<String> genres = new ArrayList<>();

            try {
                // Set up HTTP headers with authorization
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("accept", "application/json");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Fetch movie details including genres
                String url = baseUrl + "/movie/" + movieId + "?language=en-US";
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<Map<String, Object>> genresList = (List<Map<String, Object>>) response.getBody().get("genres");

                    if (genresList != null) {
                        for (Map<String, Object> genre : genresList) {
                            String genreName = (String) genre.get("name");
                            if (genreName != null) {
                                genres.add(genreName);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching genres for movie " + movieId + ": " + e.getMessage());
            }

            return genres;
        }

        /**
         * Convert a Movie entity to a MovieDTO
         * @param movie the movie entity
         * @return the movie DTO
         */
        private MovieDTO convertToDto(Movie movie) {
            if (movie == null) {
                return null;
            }

            String posterUrl = null;
            if (movie.getPosterPath() != null && !movie.getPosterPath().isBlank()) {
                posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            }

            return new MovieDTO(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getOverview(),
                    posterUrl,
                    movie.getReleaseDate(),
                    movie.getGenres()
            );
        }
    }
