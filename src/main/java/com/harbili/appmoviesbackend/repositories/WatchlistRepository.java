package com.harbili.appmoviesbackend.repositories;

import com.harbili.appmoviesbackend.entities.Watchlist;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WatchlistRepository   extends CrudRepository<Watchlist, Integer> {

    Watchlist findByTitle(String title);
    List<Watchlist> findByUserId(Long userId);


}


