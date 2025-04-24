package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findByTitle(String title);
    Movie findByPosterPath(String posterPath);
    Movie findByOverview(String overview);

}
