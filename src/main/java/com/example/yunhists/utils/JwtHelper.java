package com.example.yunhists.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    private static final String tokenSignKey = "YunnanHistoryThesisSystem2023YunnanHistoryThesisSystem2023YunnanHistoryThesisSystem2023";

    public static String createToken(Long userId, Integer userRights) {
        long tokenExpiration = 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userRights", userRights)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static Long getUserId(String token) throws Exception {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Double userIdDouble = (Double) claims.get("userId");
        return userIdDouble.longValue();
    }

    public static Integer getUserRights(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Double userRightsDouble = (Double) claims.get("userRights");
        return userRightsDouble.intValue();
    }

    public static boolean isExpiration(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
        } catch(Exception e) {
            return true;
        }
    }
}
