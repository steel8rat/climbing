package com.mokhov.climbing.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "gyms")
public class Gym {
    @Id
    private String id;
    @Indexed
    private String yelpId;
    private boolean visible;
    @DBRef
    private User visibilityChangedBy;
}
