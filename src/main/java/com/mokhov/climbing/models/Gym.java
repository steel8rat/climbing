package com.mokhov.climbing.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Document(collection = "gyms")
public class Gym {
    @Indexed
    @Getter
    private String id;
    @Indexed
    @Getter
    @Setter
    private String yelpId;
    @Indexed
    @Getter
    @Setter
    private boolean visible;

    @DBRef
    @Getter
    @Setter
    private User visibilityChangedBy;
}
