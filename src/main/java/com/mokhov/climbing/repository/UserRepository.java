package com.mokhov.climbing.repository;

import com.mokhov.climbing.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'appleIdCredentialUser' : ?0 }")
    User findByAppleIdCredentialUser(String appleIdCredentialUser);

    @Query(value = "{ 'nickname' : ?0}", exists = true)
    boolean existsByNickname(String nickname);

}
