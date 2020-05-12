package com.mokhov.climbing.models;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInWithProviderResponse {
    User user;
    boolean newUserFlag;
}
