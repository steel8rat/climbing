package com.mokhov.climbing.models;

import lombok.Data;

@Data
public class JwtParsedUser {
    String id;
    String nickname;
    String roles;
}
