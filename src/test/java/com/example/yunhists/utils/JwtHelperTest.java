package com.example.yunhists.utils;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static com.example.yunhists.utils.JwtHelper.getSalt;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JwtHelperTest {

    Long userId = 1L;
    static String testToken = "";

    @Order(1)
    @Test
    public void createToken_anyData_success() throws IOException {
        String token = JwtHelper.createToken(userId);
        assertTrue(token.length() > 0);
        testToken = token;
    }

    @Order(2)
    @Test
    public void getUserId_validToken_success() {
        Long result = JwtHelper.getUserId(testToken);
        assertEquals(userId, result);
    }

    @Order(3)
    @Test
    public void getUserId_invalidToken_MalformedJwtException() {
        MalformedJwtException thrown = assertThrows(MalformedJwtException.class, () -> JwtHelper.getUserId("acahbjvbaiuhcnusofbviucwhrsvouaclhdfiosdhciwrasbvi"));
        assertTrue(Objects.requireNonNull(thrown.getMessage()).contains("JWT strings must contain exactly 2 period characters."));
    }

    @Order(4)
    @Test
    public void isExpiration_validToken_success() {
        assertFalse(JwtHelper.isExpiration(testToken));
    }

    @Order(5)
    @Test
    public void isExpiration_expiration2SecondsSleep1Seconds_expirationIsFalse() throws Exception {
        long tokenExpiration = 2000;
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS512, getSalt())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        sleep(1000);
        assertFalse(JwtHelper.isExpiration(token));
    }

    @Order(6)
    @Test
    public void isExpiration_expiration1SecondsSleep2Seconds_expirationIsTrue() throws Exception {
        long tokenExpiration = 1000;
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS512, getSalt())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        sleep(2000);
        assertTrue(JwtHelper.isExpiration(token));
    }

}
