package com.myhome.models;

public enum PublicationType {
    NO_PUBLIC("NO_PUBLIC"), //0
    PUBLIC_TO_FRIENDS("PUBLIC_TO_FRIENDS"), //1
    PUBLIC_TO_COORDINATION_OF_ADMIN("PUBLIC_TO_COORDINATION_OF_ADMIN"), //2
    PUBLIC_TO_COMPETITIVE("PUBLIC_TO_COMPETITIVE"), //3
    PUBLIC_TO_DELETE("PUBLIC_TO_DELETE"), //4
    PUBLIC_FOR_ALL("PUBLIC_FOR_ALL"); //5 composition
    String name;

    PublicationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
