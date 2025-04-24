package com.harbili.appmoviesbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity @AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Movie {
    @Id
    private Long id; // ID de TMDb

    private String title;
    private String overview;
    private String posterPath;
    private LocalDate releaseDate;

    @ElementCollection
    private List<String> genres;


}