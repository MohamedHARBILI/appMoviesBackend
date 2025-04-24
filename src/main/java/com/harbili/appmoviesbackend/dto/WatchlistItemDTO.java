package com.harbili.appmoviesbackend.dto;

import lombok.Getter;

@Getter
public final class WatchlistItemDTO {
    private final Long id;
    private final Long movieId;
    private final String movieTitle;
    private final String status;
    private final Integer rating;
    private final String notes;

    public WatchlistItemDTO(Long id, Long movieId, String movieTitle,
                            String status, Integer rating, String notes) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
    }


}