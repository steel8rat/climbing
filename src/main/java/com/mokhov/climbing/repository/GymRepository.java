package com.mokhov.climbing.repository;

import com.mokhov.climbing.models.Gym;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GymRepository extends MongoRepository<Gym, String> {

    @Query("{ 'yelpId' : { $in : ?0 } }")
    List<Gym> findAllByYelpIds(List<String> yelpIds);

    @Query("{ 'yelpId' : ?0 }")
    Optional<Gym> findByYelpId(String yelpId);

}
