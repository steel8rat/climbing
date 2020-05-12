package com.mokhov.climbing.models;

import lombok.Data;

@Data
public class AppleIdTokenPayload {
    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub; //users unique id
    private String at_hash;
    private Long auth_time;
}
