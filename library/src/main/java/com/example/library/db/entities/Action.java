package com.example.library.db.entities;

import lombok.Data;

@Data
public class Action {
    private Integer action_id;
    private String description;

    public Action(String description) {
        this.description = description;
    }

    public Action() {

    }
}
