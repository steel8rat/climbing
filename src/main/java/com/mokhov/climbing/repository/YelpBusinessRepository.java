package com.mokhov.climbing.repository;

import com.mokhov.climbing.models.YelpBusiness;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YelpBusinessRepository extends MongoRepository<YelpBusiness, String> {}
