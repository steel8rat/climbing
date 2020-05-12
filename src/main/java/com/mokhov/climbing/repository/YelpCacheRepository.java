package com.mokhov.climbing.repository;

import com.mokhov.climbing.models.YelpCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YelpCacheRepository extends MongoRepository<YelpCache, String> {
}
