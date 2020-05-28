package com.mokhov.climbing.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.google.gson.Gson;
import com.mokhov.climbing.models.Gym;
import com.mokhov.climbing.models.RequestPhotoUploadUrlResponse;
import com.mokhov.climbing.models.User;
import com.mokhov.climbing.repository.*;
import com.mokhov.climbing.services.DateTime;
import com.mokhov.climbing.services.JwtService;
import com.mokhov.climbing.services.UuidService;
import com.mokhov.climbing.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
class RouteControllerTest {

    private static final User user = new User();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private GymRepository gymRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private YelpCacheRepository yelpCacheRepository;

    @MockBean
    private YelpBusinessRepository yelpBusinessRepository;

    @MockBean
    private GymRouteRepository gymRouteRepository;

    @MockBean
    private Utils utils;

    @MockBean
    private UuidService uuidService;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private DateTime dateTime;

    @BeforeAll
    static void init() {
        user.setId("userId");
        user.setNickname("userNickname");
    }

    @WithMockUser
    @Test
    void shouldGenerateUploadUrl() throws Exception {
        String GYM_ID = "5ea8d830d1ce817a1cfe23ae";
        String FILE_ID = "4b8d8g0d1c5817a1cfe23a2";
        String fileExtension = ".jpg";
        String URL = "https://google.com";
        Gym gym = new Gym();
        gym.setId(GYM_ID);

        Date date = new Date();
        given(dateTime.getDate()).willReturn(date);
        given(gymRepository.findById(GYM_ID)).willReturn(Optional.of(gym));

        given(uuidService.generateUuid()).willReturn(FILE_ID);
        given(amazonS3.generatePresignedUrl(any())).willReturn(new java.net.URL(URL));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(RouteController.PATH + "/generateUploadUrl").header("Authorization", jwtService.generateToken(user))
                .param("gymId", GYM_ID)
                .param("fileExtension", fileExtension);

        MvcResult result = this.mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        RequestPhotoUploadUrlResponse expectedResponse = new RequestPhotoUploadUrlResponse(FILE_ID, URL);

        RequestPhotoUploadUrlResponse actualResponse = new Gson().fromJson(result.getResponse().getContentAsString(), RequestPhotoUploadUrlResponse.class);
        assertThat(expectedResponse.equals(actualResponse)).isTrue();
    }

    @WithMockUser
    @Test()
    void shouldThrowAnErrorWhenGymIsNotFound() throws Exception {
        String GYM_ID  = "111";
        given(gymRepository.findById(GYM_ID)).willReturn(Optional.empty());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(RouteController.PATH + "/generateUploadUrl").header("Authorization", jwtService.generateToken(user))
                .param("gymId", GYM_ID)
                .param("fileExtension", ".jpg");
        this.mvc.perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test()
    void shouldThrowAnErrorWhenFileExtensionNotSupported() throws Exception {
        String GYM_ID  = "111";
        given(gymRepository.findById(GYM_ID)).willReturn(Optional.empty());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(RouteController.PATH + "/generateUploadUrl").header("Authorization", jwtService.generateToken(user))
                .param("gymId", GYM_ID)
                .param("fileExtension", ".txt");
        this.mvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }


}