package com.mokhov.climbing.services;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mokhov.climbing.exceptions.AppleKyeNotFoundForToken;
import com.mokhov.climbing.models.AppleTokenResponse;

import java.io.IOException;
import java.text.ParseException;

public interface AppleSignInService {
    AppleTokenResponse obtainRefreshToken(String authorizationCode) throws IOException, UnirestException;

    boolean validateIdentityToken(String identityToken) throws AppleKyeNotFoundForToken, IOException, ParseException;
}
