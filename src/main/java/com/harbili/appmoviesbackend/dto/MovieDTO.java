package com.harbili.appmoviesbackend.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public final class MovieDTO {
    private final Long id;
    private final String title;
    private final String overview;
    private final String posterUrl;
    private final LocalDate releaseDate;
    private final List<String> genres;

    public MovieDTO(Long id, String title, String overview,
                    String posterUrl, LocalDate releaseDate,
                    List<String> genres) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.genres = genres != null ? genres : List.of(); // Initialisation safe
    }
}