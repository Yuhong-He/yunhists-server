package com.example.yunhists.controller;

import com.example.yunhists.YunhistsServerApplication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = YunhistsServerApplication.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Order(1)
    @Test
    public void login_withoutParams_400() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertEquals("", responseString);
    }

    @Order(2)
    @Test
    public void login_validUser_200Success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("yuhong@gmail.com"));
        params.put("password", Collections.singletonList("123456"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":200"));
    }

    @Order(3)
    @Test
    public void login_wrongPwd_207WrongPwd() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("yuhong@gmail.com"));
        params.put("password", Collections.singletonList("12345"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":207"));
    }

    @Order(4)
    @Test
    public void login_googleAccount_209GoogleAccount() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("alex@gmail.com"));
        params.put("password", Collections.singletonList(""));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":209"));
    }

    @Order(5)
    @Test
    public void login_emailNotRegistered_208EmailNotRegistered() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("email", Collections.singletonList("test@gmail.com"));
        params.put("password", Collections.singletonList("test"));
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseString.startsWith("{\"code\":208"));
    }

}
