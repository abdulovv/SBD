package com.example.library.db.entities;

import lombok.Data;

@Data
public class Country {
    private Integer country_id;
    private String country_name;

    public Country(Integer countryId, String countryName) {
        this.country_id = countryId;
        this.country_name = countryName;
    }

    public Country(String country_name) {
        this.country_name = country_name;
    }

    public Country() {

    }
}
