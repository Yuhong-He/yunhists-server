package com.example.yunhists.utils;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.example.yunhists.utils.JwtHelper.getSalt;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerUtilsTest {

    @Mock
    HttpServletRequest request;

    @Test
    public void getUserIdFromToken_tokenWithValidUserId_returnUserId() throws IOException {
        int userId = 1;
        String token = JwtHelper.createToken((long) userId);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(request.getHeaderNames()).thenReturn(headerNames);
        doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return headers.get((String) args[0]);
        }).when(request).getHeader("token");
        Object obj = ControllerUtils.getUserIdFromToken(request);
        assertEquals(userId, (Integer) obj);
    }

    @Test
    public void getUserIdFromToken_tokenUserIdNull_224TokenError() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String token = JwtHelper.createToken(null);
        headers.put("token", token);
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(request.getHeaderNames()).thenReturn(headerNames);
        doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return headers.get((String) args[0]);
        }).when(request).getHeader("token");
        Object obj = ControllerUtils.getUserIdFromToken(request);
        Result result = (Result) obj;
        assertEquals(224, result.getCode());
    }

    @Test
    public void getUserIdFromToken_expiredToken_223Expired() throws Exception {
        long tokenExpiration = 1000;
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", 1L)
                .signWith(SignatureAlgorithm.HS512, getSalt())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        sleep(2000);

        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(request.getHeaderNames()).thenReturn(headerNames);
        doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return headers.get((String) args[0]);
        }).when(request).getHeader("token");
        Object obj = ControllerUtils.getUserIdFromToken(request);
        Result result = (Result) obj;
        assertEquals(223, result.getCode());
    }

    @Test
    public void getUserIdFromToken_emptyToken_225MissingToken() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", "");
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(request.getHeaderNames()).thenReturn(headerNames);
        doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return headers.get((String) args[0]);
        }).when(request).getHeader("token");
        Object obj = ControllerUtils.getUserIdFromToken(request);
        Result result = (Result) obj;
        assertEquals(225, result.getCode());
    }

}
