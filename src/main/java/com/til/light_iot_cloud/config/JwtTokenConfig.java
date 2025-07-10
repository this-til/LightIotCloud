package com.til.light_iot_cloud.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.til.light_iot_cloud.data.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JwtTokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.wirelessTime}")
    private boolean wirelessTime;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret);
    }

    public String generateJwt(User user) {
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("id", user.getId());
        if (!wirelessTime) {
            builder.withExpiresAt(new Date(System.currentTimeMillis() + expiration));
        }
        return builder.sign(algorithm);
    }

    public Long parseJwt(String token) {
        DecodedJWT jwt = JWT.require(algorithm)
                .build()
                .verify(token);

        return jwt.getClaim("id").asLong();
    }

}
