package com.mokhov.climbing.models;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "routes")
public class GymRoute {
    @Id
    String id;

    final List<String> photos = new ArrayList<>();

    @DBRef
    User author;

    public void addPhoto(@NonNull String photo){
        photos.add(photo);
    }
}
