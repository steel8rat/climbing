package com.mokhov.climbing.services;

import com.mokhov.climbing.models.JwtParsedUser;
import com.mokhov.climbing.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtService {

    private static SecretKey secretKey;
    private static final SignatureAlgorithm HS_512 = SignatureAlgorithm.HS512;

    @Value("${jwt.secret}")
    private String BASE_64_ENCODED_SECRET;

    private SecretKey getPrivateKey() {
        if (secretKey == null)
            secretKey = new SecretKeySpec(Decoders.BASE64.decode(BASE_64_ENCODED_SECRET), HS_512.getJcaName());
        return secretKey;
    }

    public JwtParsedUser parseToken(@NonNull String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(getPrivateKey())
                    .parseClaimsJws(token)
                    .getBody();
            JwtParsedUser user = new JwtParsedUser();
            String userId = body.getSubject();
            assert userId != null && userId.length() > 0;
            user.setId(userId);
            user.setNickname((String) body.get("nickname"));
            user.setRoles((String) body.get("roles"));
            return user;
        } catch (JwtException | ClassCastException e) {
            return null;
        }
    }

    public String generateToken(@NonNull User user) {
        assert user.getId() != null && user.getId().length() > 0;
        Claims claims = Jwts.claims().setSubject(user.getId());
        claims.put("nickname", user.getNickname());
        String commaSeparatedAuthoritiesString = user.getAuthorities().toString().substring(1);
        commaSeparatedAuthoritiesString = commaSeparatedAuthoritiesString.substring(0, commaSeparatedAuthoritiesString.length() - 1);
        claims.put("roles", commaSeparatedAuthoritiesString);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getPrivateKey(), HS_512)
                .compact();
    }
}
