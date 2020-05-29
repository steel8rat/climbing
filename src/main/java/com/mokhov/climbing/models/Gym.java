package com.mokhov.climbing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "gyms")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gym{
    @Id
    private String id;
    private String name;
    private String city;
    @Indexed
    private String yelpId;
    @Indexed
    private String googleId;

    private Boolean visible;
    private double distance;
    @JsonIgnore
    @DBRef
    private User visibilityChangedBy;
    private Coordinates coordinates;

    // YELP specific fields (not saved to mongo)
    @Transient
    private String yelpName;
    @Transient
    private String yelpImageUrl;
    @Transient
    private String yelpUrl;
    @Transient
    private Integer yelpReviewCount;
    @Transient
    private Double yelpRating;
    @Transient
    private Coordinates yelpCoordinates;
    @Transient
    private String yelpCity;
    @Transient
    private Double yelpDistance;

    public void loadPropertiesFromYelp(YelpBusiness yelpBusiness){
        yelpId = yelpBusiness.getId();
        yelpName = yelpBusiness.getName();
        yelpImageUrl = yelpBusiness.getYelpImageUrl();
        yelpUrl = yelpBusiness.getYelpUrl();
        yelpReviewCount = yelpBusiness.getYelpReviewCount();
        yelpRating = yelpBusiness.getYelpRating();
        yelpCoordinates = yelpBusiness.getCoordinates();
        yelpCity = yelpBusiness.getLocation().getCity();
        yelpDistance = yelpBusiness.getDistance();
    }
}
