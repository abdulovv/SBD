package com.example.library.db.entities;

import lombok.Data;

@Data
public class Genre {
    private Integer genre_id;
    private String genre_name;

    public Genre(Integer genre_id, String genre_name) {
        this.genre_id = genre_id;
        this.genre_name = genre_name;
    }

    public Genre() {
    }
}
