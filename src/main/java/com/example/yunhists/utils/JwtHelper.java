package com.example.yunhists.utils;

import io.jsonwebtoken.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class JwtHelper {

    public static String createToken(Long userId) throws IOException {
        long tokenExpiration = 60 * 60 * 1000; // 1 hour
        return Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS512, getSalt())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static Long getUserId(String token) throws IOException {
        if(token.isEmpty()) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getSalt()).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Double userIdDouble = (Double) claims.get("userId");
        return userIdDouble.longValue();
    }

    public static boolean isExpiration(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSalt())
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
        } catch(Exception e) {
            return true;
        }
    }

    public static String getSalt() throws IOException {
        Properties props = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(DirectMailUtils.class.getClassLoader().getResourceAsStream("securityKey.properties")),
                StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        return props.getProperty("jwt.salt");
    }
}
