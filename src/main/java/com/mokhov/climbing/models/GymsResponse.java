package com.mokhov.climbing.models;

import com.mokhov.climbing.enumerators.GymProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Data
@RequiredArgsConstructor
public class GymsResponse {
    private final GymProvider provider;
    private final boolean cached;
    private final List<YelpBusiness> businesses;
}
