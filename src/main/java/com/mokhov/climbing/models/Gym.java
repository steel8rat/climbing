package com.mokhov.climbing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Gym{
    @Id
    private String id;
    private String name;
    private String city;
    @Indexed
    private String yelpId;
    private Boolean visible;
    private double distance;
    @JsonIgnore
    @DBRef
    private User visibilityChangedBy;

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
    private YelpCoordinates yelpCoordinates;


    public void loadPropertiesFromYelp(YelpBusiness yelpBusiness){
        yelpName = yelpBusiness.getYelpName();
        yelpImageUrl = yelpBusiness.getYelpImageUrl();
        yelpUrl = yelpBusiness.getYelpUrl();
        yelpReviewCount = yelpBusiness.getYelpReviewCount();
        yelpRating = yelpBusiness.getYelpRating();
        yelpCoordinates = yelpBusiness.getCoordinates();
        city = yelpBusiness.getLocation().getCity();
        distance = yelpBusiness.getDistance();
    }

}
