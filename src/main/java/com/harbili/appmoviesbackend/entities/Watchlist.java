package com.harbili.appmoviesbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity   @AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Watchlist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL)
    private List<WatchlistItem> items;


}