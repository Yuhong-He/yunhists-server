package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerUtilsTest {

    @Mock
    HttpServletRequest request;

    @Test
    public void getUserIdFromToken_tokenWithValidUserId_returnUserId() {
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
    public void getUserIdFromToken_invalidToken_224TokenError() {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", "huvionaiuvoniovnirueaonvuoea");
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
