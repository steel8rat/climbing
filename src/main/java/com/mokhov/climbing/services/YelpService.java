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
    private String YELP_TOKEN;
    @Value("${yelp.search.url}")
    private String YELP_SEARCH_URL;
    @Value("${yelp.search.radius}")
    private String YELP_SEARCH_RADIUS;
    @Value("${yelp.search.categories}")
    private String YELP_SEARCH_CATEGORIES;
    @Value("${yelp.search.term}")
    private String YELP_SEARCH_TERM;
    @Value("${yelp.search.limit}")
    private String YELP_SEARCH_LIMIT;

    public YelpSearchResponse search(String latitude, String longitude) throws UnirestException {
        HttpResponse<String> response = Unirest.get(YELP_SEARCH_URL)
                .header("Authorization", "Bearer " + YELP_TOKEN)
                .queryString("latitude", latitude)
                .queryString("longitude", longitude)
                .queryString("term", YELP_SEARCH_TERM)
                .queryString("categories", YELP_SEARCH_CATEGORIES)
                .queryString("radius", YELP_SEARCH_RADIUS)
                .queryString("sort_by", "distance")
                .queryString("limit", YELP_SEARCH_LIMIT).asString();
        //TODO add warning, maybe an email or slack if ratelimit-remaining is below a certain threshold
        //System.out.print(response.getHeaders().getFirst("ratelimit-remaining"));
        //TODO think about the case when get request to Yelp fails
        return new Gson().fromJson(response.getBody(), YelpSearchResponse.class);
    }

}
