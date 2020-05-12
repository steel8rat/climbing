package com.mokhov.climbing.services;

import com.mokhov.climbing.models.AppleTokenResponse;

public interface AppleSignInService {
    AppleTokenResponse obtainRefreshToken(String authorizationCode) throws Exception;

    boolean validateIdentityToken(String identityToken) throws Exception;
}
