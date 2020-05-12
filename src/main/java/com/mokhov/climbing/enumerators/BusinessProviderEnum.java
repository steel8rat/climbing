package com.mokhov.climbing.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum BusinessProviderEnum {
    INTERNAL("internal"),
    YELP("yelp"),
    GOOGLE("google");

    @Getter
    private final String name;

}
