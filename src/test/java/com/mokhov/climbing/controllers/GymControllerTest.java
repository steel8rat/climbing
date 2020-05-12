package com.mokhov.climbing.controllers;

import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mokhov.climbing.enumerators.BusinessProviderEnum;
import com.mokhov.climbing.models.*;
import com.mokhov.climbing.repository.GymRepository;
import com.mokhov.climbing.repository.UserRepository;
import com.mokhov.climbing.repository.YelpBusinessRepository;
import com.mokhov.climbing.repository.YelpCacheRepository;
import com.mokhov.climbing.services.JwtService;
import com.mokhov.climbing.services.YelpService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.util.*;

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
class GymControllerTest {

    private static final User adminUser = new User();


    @MockBean
    private YelpBusinessRepository yelpBusinessRepository;


    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private YelpCacheRepository yelpCacheRepository;

    @MockBean
    private YelpService yelpService;

    @MockBean
    private GymRepository gymRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeAll
    static void init(){
        adminUser.setId("adminUserId");
        adminUser.setNickname("adminUserNickname");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        adminUser.setAuthorities(authorities);
    }

    @Test
    void getGymsAndSortByDistanceWhenCacheIsFound() throws Exception {
        List<YelpBusiness> yelpBusinesses = new ArrayList<>();
        YelpBusiness business1 = new YelpBusiness();
        business1.setCoordinates(new YelpCoordinates(43.375677, -79.998647));
        business1.setId("business1");
        yelpBusinesses.add(business1);

        YelpBusiness business2 = new YelpBusiness();
        business2.setCoordinates(new YelpCoordinates(43.675677, -79.398656));
        business2.setId("business2");
        yelpBusinesses.add(business2);

        YelpBusiness business3 = new YelpBusiness();
        business3.setCoordinates(new YelpCoordinates(43.005636, -79.008647));
        business3.setId("business3");
        yelpBusinesses.add(business3);

        YelpBusiness business4 = new YelpBusiness();
        business4.setCoordinates(new YelpCoordinates(44.005636, -69.008647));
        business4.setId("business4");
        yelpBusinesses.add(business4);

        // Create expected response
        YelpCache yelpCache = new YelpCache("43.7,-79.4", yelpBusinesses);

        // Mock yelp cache repository
        given(yelpCacheRepository.findById("43.7,-79.4")).willReturn(Optional.of(yelpCache));

        // Mock gym repository with one hidden gym
        List<Gym> hiddenGyms = new ArrayList<>();
        Gym hiddenGym = new Gym();
        hiddenGym.setYelpId("business4");
        hiddenGyms.add(hiddenGym);
        given(gymRepository.findAllByYelpIds(Arrays.asList("business1", "business2", "business3", "business4"))).willReturn(hiddenGyms);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GymController.PATH)
                .param("latitude", "43.695636")
                .param("longitude", "-79.398647");

        InputStream is = currentThread().getContextClassLoader().getResourceAsStream("gyms-test-response-cache-is-found.json");
        String expectedJson = IOUtils.toString(Objects.requireNonNull(is));

        MvcResult result = this.mvc.perform(requestBuilder)

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonObject json = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(json);

        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @WithMockUser
    public void shouldNotAllowPrivateEndpointsForAuthorizedNotAdminUser() throws Exception {
        this.mvc.perform(delete(GymController.PATH + "/cache/yelp")).andExpect(status().isUnauthorized());
        this.mvc.perform(patch(GymController.PATH + "/some_id")).andExpect(status().isUnauthorized());
    }

    @Test
    void getGymsAndSortByDistanceWhenCacheIsNotFound() throws Exception {
        List<YelpBusiness> yelpBusinesses = new ArrayList<>();
        YelpBusiness business1 = new YelpBusiness();
        business1.setCoordinates(new YelpCoordinates(43.375677, -79.998647));
        business1.setId("business1");
        yelpBusinesses.add(business1);

        YelpBusiness business2 = new YelpBusiness();
        business2.setCoordinates(new YelpCoordinates(43.675677, -79.398656));
        business2.setId("business2");
        yelpBusinesses.add(business2);

        YelpBusiness business3 = new YelpBusiness();
        business3.setCoordinates(new YelpCoordinates(43.005636, -79.008647));
        business3.setId("business3");
        yelpBusinesses.add(business3);

        // Hidden gym
        YelpBusiness business4 = new YelpBusiness();
        business4.setCoordinates(new YelpCoordinates(44.005636, -69.008647));
        business4.setId("business4");
        yelpBusinesses.add(business4);

        // Create expected response
        YelpSearchResponse yelpSearchResponse = new YelpSearchResponse();
        yelpSearchResponse.setBusinesses(yelpBusinesses);
        given(yelpCacheRepository.findById("43.7,-79.4")).willReturn(Optional.empty());
        given(yelpService.search("43.7", "-79.4")).willReturn(yelpSearchResponse);
//        given(yelpCacheRepository.save(yelpCache)).willReturn(null);
//        given(yelpBusinessRepository.saveAll(yelpCache.getBusinesses())).willReturn(null);

        // Mock gym repository with one hidden gym
        List<Gym> hiddenGyms = new ArrayList<>();
        Gym hiddenGym = new Gym();
        hiddenGym.setVisible(false);
        hiddenGym.setYelpId("business4");
        hiddenGyms.add(hiddenGym);
        given(gymRepository.findAllByYelpIds(Arrays.asList("business1", "business2", "business3", "business4"))).willReturn(hiddenGyms);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GymController.PATH )
                .param("latitude", "43.695636")
                .param("longitude", "-79.398647");

        InputStream is = currentThread().getContextClassLoader().getResourceAsStream("gyms-test-response.json");
        String expectedJson = IOUtils.toString(Objects.requireNonNull(is));

        MvcResult result = this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonObject json = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String actualJson = gson.toJson(json);

        assertThat(actualJson).isEqualTo(expectedJson);

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowToPurgeYelpCacheWithCoordinates() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(GymController.PATH + "/cache/yelp").header("Authorization", jwtService.generateToken(adminUser))
                .param("latitude", "43.695636")
                .param("longitude", "-79.398647");
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowToPurgeYelpCacheWithoutCoordinates() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(GymController.PATH + "/cache/yelp").header("Authorization", jwtService.generateToken(adminUser));
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowToChangeGymVisibilityWhenYelpProvider() throws Exception {
        Gym gym = new Gym();
        gym.setYelpId("yelp_id");
        given(gymRepository.findByYelpId("yelp_id")).willReturn(Optional.of(gym));
        User user = new User();
        user.setId(adminUser.getId());
        given(userRepository.findById(adminUser.getId())).willReturn(Optional.of(user));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(GymController.PATH + "/yelp_id").header("Authorization", jwtService.generateToken(adminUser))
                .param("provider", BusinessProviderEnum.YELP.getName().toUpperCase())
                .param("visibility", "false");
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowErrorWhenYelpBusinessIsNotFound() {
        Gym gym = new Gym();
        gym.setYelpId("yelp_id");
        given(gymRepository.findByYelpId("yelp_id")).willReturn(Optional.empty());
        User user = new User();
        user.setId(adminUser.getId());
        given(userRepository.findById(adminUser.getId())).willReturn(Optional.of(user));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(GymController.PATH + "/yelp_id").header("Authorization", jwtService.generateToken(adminUser))
                .param("provider", BusinessProviderEnum.YELP.getName().toUpperCase())
                .param("visibility", "false");
        try {
            this.mvc.perform(requestBuilder);
        } catch (Exception e) {
            assertThat(e.getCause().getMessage()).isEqualTo("Yelp business not found");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowErrorWhenInternalBusinessIsNotFound() {
        Gym gym = new Gym();
        gym.setYelpId("yelp_id");
        given(gymRepository.findByYelpId("yelp_id")).willReturn(Optional.empty());
        User user = new User();
        user.setId(adminUser.getId());
        given(userRepository.findById(adminUser.getId())).willReturn(Optional.of(user));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(GymController.PATH + "/yelp_id").header("Authorization", jwtService.generateToken(adminUser))
                .param("provider", BusinessProviderEnum.INTERNAL.getName().toUpperCase())
                .param("visibility", "false");
        try {
            this.mvc.perform(requestBuilder);
        } catch (Exception e) {
            assertThat(e.getCause().getMessage()).isEqualTo("Gym not found");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowToChangeGymVisibilityWhenInternalProvider() throws Exception {
        Gym gym = new Gym();
        gym.setYelpId("gym_id");
        given(gymRepository.findById("gym_id")).willReturn(Optional.of(gym));
        User user = new User();
        user.setId(adminUser.getId());
        given(userRepository.findById(adminUser.getId())).willReturn(Optional.of(user));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(GymController.PATH + "/gym_id").header("Authorization", jwtService.generateToken(adminUser))
                .param("provider", BusinessProviderEnum.INTERNAL.getName().toUpperCase())
                .param("visibility", "false");
        this.mvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

}