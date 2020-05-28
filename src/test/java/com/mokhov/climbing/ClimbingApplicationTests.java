package com.mokhov.climbing;

import com.mokhov.climbing.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
class ClimbingApplicationTests {

    @MockBean
    private GymRepository gymRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private YelpBusinessRepository yelpBusinessRepository;

    @MockBean
    private YelpCacheRepository yelpCacheRepository;

    @MockBean
    private GymRouteRepository gymRouteRepository;
}
