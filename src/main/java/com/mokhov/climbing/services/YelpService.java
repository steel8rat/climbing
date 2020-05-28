package com.mokhov.climbing.services;

import com.mokhov.climbing.models.YelpSearchResponse;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YelpService {

    @Value("${yelp.token}")
    private String yelpToken;
    @Value("${yelp.search.url}")
    private String yelpSearchUrl;
    @Value("${yelp.search.radius}")
    private String yelpSearchRadius;
    @Value("${yelp.search.categories}")
    private String yelpSearchCategories;
    @Value("${yelp.search.term}")
    private String yelpSearchTerm;
    @Value("${yelp.search.limit}")
    private String yelpSearchLimit;

    public YelpSearchResponse search(String latitude, String longitude) throws UnirestException {
        HttpResponse<String> response = Unirest.get(yelpSearchUrl)
                .header("Authorization", "Bearer " + yelpToken)
                .queryString("latitude", latitude)
                .queryString("longitude", longitude)
                .queryString("term", yelpSearchTerm)
                .queryString("categories", yelpSearchCategories)
                .queryString("radius", yelpSearchRadius)
                .queryString("sort_by", "distance")
                .queryString("limit", yelpSearchLimit).asString();
        // add warning, maybe an email or slack if ratelimit-remaining is below a certain threshold
        // think about the case when get request to Yelp fails
        return new Gson().fromJson(response.getBody(), YelpSearchResponse.class);
    }

}
