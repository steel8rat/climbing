package com.mokhov.climbing.models;


import lombok.Getter;

public class JwtHeader {
    @Getter
    String kid;
    String alg;
}
