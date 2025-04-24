package com.harbili.appmoviesbackend.dto;

import java.util.List;

public class WatchlistDTO {
    private Long id;
    private String name;
    private String description;
    private List<WatchlistItemDTO> items;

    // Constructeur
    public WatchlistDTO(Long id, String name, String description, List<WatchlistItemDTO> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items;
    }

    // Getters (pas de setters pour garder l'immuabilité)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<WatchlistItemDTO> getItems() { return items; }

    // Optionnel : toString(), equals(), hashCode()
}