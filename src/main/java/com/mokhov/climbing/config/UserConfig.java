package com.mokhov.climbing.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class UserConfig {

    @Value("${user.nickname.max-char}")
    private int nicknameMaxChar;

    @Value("${user.nickname.allowed-char}")
    private String nicknameAllowedChar;

    @Value("${user.full-name.max-char}")
    private int fullNameMaxChar;
}
