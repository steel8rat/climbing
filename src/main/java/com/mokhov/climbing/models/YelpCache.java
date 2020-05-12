package com.mokhov.climbing.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "yelpResponses")
@RequiredArgsConstructor
public class YelpCache {

    @Id
    private final String id;

    @DBRef
    @SerializedName("yelpBusinesses")
    private final List<YelpBusiness> businesses;

    @Indexed(name = "created", expireAfterSeconds = 86400) //document is automatically deleted after 24hrs
    private Date created;

}


