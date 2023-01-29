package com.myhome.models;

public enum Genre {
    FANTASY("Фантастика"),
    NOVEL("Романи"),
    TALE("Казки"),
    STORIES("Історії");

    String name;

    Genre(String name) {
        this.name = name;
    }
}
