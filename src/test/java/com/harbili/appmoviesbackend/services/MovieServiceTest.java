package com.harbili.appmoviesbackend.services;

import com.harbili.appmoviesbackend.dto.MovieDTO;
import com.harbili.appmoviesbackend.entities.Movie;
import com.harbili.appmoviesbackend.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllMovies_whenDatabaseEmpty_shouldCreateSampleMovies() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        // Mock saving to database
        List<Movie> sampleMovies = new ArrayList<>();
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Sample Movie 1");
        sampleMovies.add(movie1);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Sample Movie 2");
        sampleMovies.add(movie2);

        when(movieRepository.saveAll(any())).thenReturn(sampleMovies);

        // Act
        List<MovieDTO> result = movieService.findAllMovies();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(movieRepository).findAll();
        verify(movieRepository).saveAll(any());

        System.out.println("[DEBUG_LOG] Test completed. Result size: " + result.size());
    }
}
