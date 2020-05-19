package com.mokhov.climbing.repository;


import com.mokhov.climbing.models.GymRoute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GymRouteRepository extends MongoRepository<GymRoute, String> {

}
