package com.mokhov.climbing.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum GymProvider {
    INTERNAL("internal"),
    YELP("yelp"),
    GOOGLE("google");

    @Getter
    private final String name;

}
