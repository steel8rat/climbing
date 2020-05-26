package com.mokhov.climbing.services;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mokhov.climbing.exceptions.AppleKyeNotFoundForToken;
import com.mokhov.climbing.models.AppleTokenResponse;
import com.mokhov.climbing.models.JwtHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyConverter;
import io.jsonwebtoken.*;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class AppleSignInServiceImpl implements AppleSignInService {
    private static final String CLIENT_ID = "com.mokhov.climbing.app";
    private static final String APPLE_URL = "https://appleid.apple.com";
    private static PrivateKey pKey;

    private static PrivateKey getPrivateKey() throws IOException {
        if (pKey == null) {
            InputStream stream = new ClassPathResource("apple/AuthKey.p8").getInputStream();
            final PEMParser pemParser = new PEMParser(new InputStreamReader(stream));
            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            pKey = converter.getPrivateKey(object);
        }
        return pKey;
    }

    private static String generateJWT() throws IOException {
        String keyId = "V2X4QG6J6M";
        String teamId = "5LM8Z3E6DQ";
        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, keyId)
                .setIssuer(teamId)
                .setAudience(APPLE_URL)
                .setSubject(CLIENT_ID)
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    @Override
    public AppleTokenResponse obtainRefreshToken(String authorizationCode) throws IOException, UnirestException {
        String appleAuthUrl = APPLE_URL + "/auth/token";
        HttpResponse<String> response = Unirest.post(appleAuthUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", CLIENT_ID)
                .field("client_secret", generateJWT())
                .field("grant_type", "authorization_code")
                .field("code", authorizationCode)
                .asString();
        return new Gson().fromJson(response.getBody(), AppleTokenResponse.class);
    }


    @Override
    public boolean validateIdentityToken(String identityToken) throws AppleKyeNotFoundForToken, IOException, ParseException {
        boolean valid = false;
        //Get key id
        String tokenHeaderString = new String(Base64.getDecoder().decode((identityToken.split("\\.")[0])));
        JwtHeader tokenHeaderObject = (new Gson()).fromJson(tokenHeaderString, JwtHeader.class);
        String kid = tokenHeaderObject.getKid();
        //Fetch keys from Apple
        JWKSet jwkSet = JWKSet.load(new URL(APPLE_URL + "/auth/keys"));
        //Find key with key equal to kid
        List<JWK> keys = new ArrayList<>(jwkSet.getKeys());
        keys.removeIf(key -> (!kid.equals(key.getKeyID())));
        if (keys.isEmpty())
            throw new AppleKyeNotFoundForToken("No Apple key found for token identity");
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(KeyConverter.toJavaKeys(keys).get(0)).build();
        Jws<Claims> jwtClaims = jwtParser.parseClaimsJws(identityToken);
        Claims body = jwtClaims.getBody();
        assert body != null;
        if (body.getIssuer().equals(APPLE_URL) || body.getAudience().equals(CLIENT_ID) || body.getExpiration().before(new Date())
        ) valid = true;
        return valid;
    }
}
