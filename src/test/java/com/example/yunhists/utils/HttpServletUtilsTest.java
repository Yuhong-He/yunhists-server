package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpServletUtilsTest {

    @Mock
    HttpServletRequest request;

    @Test
    public void getToken_tokenHasValue_canGetToken() {
        String token = "hvrskbvasvnicrabvlksbvnierani";
        Map<String, String> headers = new HashMap<>();
        headers.put("token", token);
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        when(request.getHeaderNames()).thenReturn(headerNames);
        doAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return headers.get((String) args[0]);
        }).when(request).getHeader("token");
        String result = HttpServletUtils.getToken(request);
        assertEquals(token, result);
        assertTrue(result.length() > 0);
    }

}
