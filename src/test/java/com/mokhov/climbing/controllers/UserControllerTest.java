package com.mokhov.climbing.controllers;

import com.mokhov.climbing.models.*;
import com.mokhov.climbing.repository.GymRepository;
import com.mokhov.climbing.repository.UserRepository;
import com.mokhov.climbing.repository.YelpBusinessRepository;
import com.mokhov.climbing.repository.YelpCacheRepository;
import com.mokhov.climbing.services.AppleSignInServiceImpl;
import com.mokhov.climbing.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AppleSignInServiceImpl appleSignInService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GymRepository gymRepository;

    @MockBean
    private YelpBusinessRepository yelpBusinessRepository;

    @MockBean
    private YelpCacheRepository yelpCacheRepository;

    @Test
    public void shouldNOtAllowPrivateEndpoints() throws Exception {
        this.mvc.perform(get(UserController.PATH+ "/logout")).andExpect(status().isUnauthorized());
        this.mvc.perform(get(UserController.PATH)).andExpect(status().isUnauthorized());
        this.mvc.perform(post(UserController.PATH)).andExpect(status().isUnauthorized());
        this.mvc.perform(get(UserController.PATH+ "/requestUploadPhotoUrl")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldAllowAccessToSignInWithGoogle() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        given(jwtService.generateToken(new User())).willReturn("eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJudWxsIiwicm9sZXMiOiIifQ.tCx85PwKoEc-m_qccS0buRhTq3XvrmO2RZPBFWDkB297qxv3mQbgoQ2oBp_hGoYG53Jx0MGHIQvXFBvLgbpoGg");
        User user = new User();
        user.setToken("eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJudWxsIiwicm9sZXMiOiIifQ.tCx85PwKoEc-m_qccS0buRhTq3XvrmO2RZPBFWDkB297qxv3mQbgoQ2oBp_hGoYG53Jx0MGHIQvXFBvLgbpoGg");
        this.mvc.perform(MockMvcRequestBuilders
                .post(UserController.PATH+ "/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new GoogleIdCredentials())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(new SignInWithProviderResponse(user, true))));
    }

    @Test
    public void shouldAllowAccessToSignInWithApple() throws Exception {
        given(appleSignInService.validateIdentityToken("identity_token")).willReturn(true);
        AppleTokenResponse appleTokenResponse = new AppleTokenResponse();
        appleTokenResponse.setRefreshToken("refresh_token");
        given(appleSignInService.obtainRefreshToken("authorization_code")).willReturn(appleTokenResponse);
        User findByAppleIdCredentialUser = new User();
        given(userRepository.findByAppleIdCredentialUser("user_apple_id")).willReturn(findByAppleIdCredentialUser);
        given(jwtService.generateToken(findByAppleIdCredentialUser)).willReturn("eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJudWxsIiwicm9sZXMiOiIifQ.tCx85PwKoEc-m_qccS0buRhTq3XvrmO2RZPBFWDkB297qxv3mQbgoQ2oBp_hGoYG53Jx0MGHIQvXFBvLgbpoGg");
        ObjectMapper mapper = new ObjectMapper();
        AppleIdCredential appleIdCredential =  new AppleIdCredential();
        appleIdCredential.setIdentityToken("identity_token");
        appleIdCredential.setAuthorizationCode("authorization_code");
        appleIdCredential.setUser("user_apple_id");
        User user = new User();
        user.setAppleRefreshToken("refresh_token");
        User expectedUser = new User();
        expectedUser.setToken("eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJudWxsIiwicm9sZXMiOiIifQ.tCx85PwKoEc-m_qccS0buRhTq3XvrmO2RZPBFWDkB297qxv3mQbgoQ2oBp_hGoYG53Jx0MGHIQvXFBvLgbpoGg");
        this.mvc.perform(MockMvcRequestBuilders
                .post( UserController.PATH+ "/login/apple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(appleIdCredential)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(new SignInWithProviderResponse(expectedUser, false))));
    }

}