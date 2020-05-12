package com.mokhov.climbing.models;

import lombok.Data;

@Data
public class AppleIdCredential {
    // A JSON Web Token (JWT) that securely communicates information about the user to your app. Can be null.
    private String identityToken;
    // A short-lived token used by your app for proof of authorization when interacting with the app’s server counterpart. Can be null.
    private String authorizationCode;
    private String user;
    private String email;
    private ApplePersonNameComponents fullName;
}
