package com.mokhov.climbing.models;

import lombok.Data;

import java.util.List;

@Data
public class YelpSearchResponse {
    private List<YelpBusiness> businesses = null;
}
