package com.example.yunhists.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class JwtHelper {

    public static String createAccessToken(Long userId) {
        long tokenExpiration = 20 * 1000; // 50 minutes
        return createToken(tokenExpiration, userId);
    }

    public static long getExpiredTime() {
        long tokenExpiration = 20 * 1000; // 50 minutes
        return System.currentTimeMillis() + tokenExpiration;
    }

    public static String createRefreshToken(Long userId) {
        long tokenExpiration = 365L * 24 * 60 * 60 * 1000; // 1 year
        return createToken(tokenExpiration, userId);
    }

    private static String createToken(long tokenExpiration, long userId) {
        try {
            return Jwts.builder()
                    .setSubject("YUNHISTS-USER")
                    .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                    .claim("userId", userId)
                    .signWith(getKey(), SignatureAlgorithm.HS512)
                    .compressWith(CompressionCodecs.GZIP)
                    .compact();
        } catch (Exception e){
            log.error("Jwt error: " + e.getMessage());
            return null;
        }
    }

    public static Long getUserId(String token) {
        try {
            if(token.isEmpty()) return null;
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Double userIdDouble = (Double) claims.get("userId");
            return userIdDouble.longValue();
        } catch (Exception e) {
            log.error("Jwt error: " + e.getMessage());
            return null;
        }
    }

    public static boolean notExpired(String token) {
        try {
            return !Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token)
                    .getBody().getExpiration().before(new Date());
        } catch(Exception e) {
            log.error("Jwt error: " + e.getMessage());
            return false;
        }
    }

    private static Key getKey() throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DirectMailUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        byte[] signingKey = props.getProperty("jwt.salt").getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(signingKey, SignatureAlgorithm.HS512.getJcaName());
    }
}
