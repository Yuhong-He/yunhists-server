package com.example.yunhists.controller;

import com.example.yunhists.YunhistsServerApplication;
import com.example.yunhists.utils.JwtHelper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = YunhistsServerApplication.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Before()
    public void setup () {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Transactional
    @Test
    public void login_invalidUser_208EmailNotRegistered() throws Exception {
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .get("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "test@email.com")
                .param("password", "123456");

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":208"));
    }

    @Transactional
    @Test
    public void google_notRegistered_success() throws Exception {
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .get("/api/user/google")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "test@email.com")
                .param("username", "test_user")
                .param("lang", "zh");

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":200"));
    }

    @Transactional
    @Test
    public void register_notSendEmailBefore_218NoVerificationCode() throws Exception {
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lang", "zh")
                .param("email", "test@email.com")
                .param("username", "test_user")
                .param("password", "123456")
                .param("password2", "123456")
                .param("code", "123456");

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":218"));
    }

    @Transactional
    @Test
    public void getUserInfo_InvalidUserId_223TokenExpired() throws Exception {
        String token = "token";
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .post("/api/user/getUserInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("access_token", token);

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":223"));
    }

    @Transactional
    @Test
    public void getUserInfo_InvalidUserId_225TokenMissing() throws Exception {
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .post("/api/user/getUserInfo")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":225"));
    }

    @Transactional
    @Test
    public void getUserInfo_InvalidUser_205UserNotExist() throws Exception {
        String token = JwtHelper.createAccessToken(999999L);
        MockHttpServletRequestBuilder postRequestBuilder = MockMvcRequestBuilders
                .post("/api/user/getUserInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("access_token", token);

        MvcResult response =  mockMvc.perform(postRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().startsWith("{\"code\":205"));
    }

}
